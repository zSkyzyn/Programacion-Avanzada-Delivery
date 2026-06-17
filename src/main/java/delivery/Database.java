package delivery;

import delivery.model.*;
import delivery.tier.*;
import delivery.model.vehiculo.*;

import java.sql.*;
import java.util.*;

/**
 * Clase central de acceso a datos.
 * Reemplaza todos los DAOs y la configuración ORM.
 *
 * Usa JDBC directamente con SQLite: sin anotaciones, sin XML, sin magia.
 */
public class Database {

    private static final String URL = "jdbc:sqlite:delivery.db";

    // ─── Conexión ────────────────────────────────────────────────────────────

    static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // ─── Inicialización ──────────────────────────────────────────────────────

    /** Crea las tablas si no existen y carga los datos de prueba. */
    public static void init() {
        try (Connection c = connect(); Statement s = c.createStatement()) {

            s.execute("""
                CREATE TABLE IF NOT EXISTS usuarios (
                    id      INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre  TEXT NOT NULL,
                    email   TEXT NOT NULL UNIQUE,
                    telefono TEXT,
                    tier    TEXT NOT NULL DEFAULT 'NORMAL'
                )""");

            s.execute("""
                CREATE TABLE IF NOT EXISTS productos (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre      TEXT NOT NULL,
                    descripcion TEXT,
                    precio      REAL NOT NULL
                )""");

            s.execute("""
                CREATE TABLE IF NOT EXISTS repartidores (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre      TEXT NOT NULL,
                    vehiculo    TEXT NOT NULL,
                    disponible  INTEGER NOT NULL DEFAULT 1
                )""");

            s.execute("""
                CREATE TABLE IF NOT EXISTS pedidos (
                    id           INTEGER PRIMARY KEY AUTOINCREMENT,
                    estado       TEXT NOT NULL DEFAULT 'PENDIENTE',
                    metodo_envio TEXT NOT NULL,
                    costo_envio  REAL NOT NULL,
                    usuario_id   INTEGER NOT NULL REFERENCES usuarios(id),
                    repartidor_id INTEGER REFERENCES repartidores(id)
                )""");

            s.execute("""
                CREATE TABLE IF NOT EXISTS pedido_productos (
                    pedido_id   INTEGER REFERENCES pedidos(id),
                    producto_id INTEGER REFERENCES productos(id),
                    PRIMARY KEY (pedido_id, producto_id)
                )""");

            seedData(c);
            System.out.println("Base de datos lista.");

        } catch (SQLException e) {
            throw new RuntimeException("Error al inicializar la base de datos", e);
        }
    }

    /** Inserta datos de prueba solo si las tablas están vacías. */
    private static void seedData(Connection c) throws SQLException {
        if (count(c, "usuarios") > 0) return;

        System.out.println("Cargando datos de prueba...");
        exec(c, "INSERT INTO usuarios(nombre,email,telefono,tier) VALUES(?,?,?,?)",
                "Ana García",    "ana@mail.com",  "1122334455", "NORMAL");
        exec(c, "INSERT INTO usuarios(nombre,email,telefono,tier) VALUES(?,?,?,?)",
                "Luis Martínez", "luis@mail.com", "1166778899", "PLATA");
        exec(c, "INSERT INTO usuarios(nombre,email,telefono,tier) VALUES(?,?,?,?)",
                "María López",   "maria@mail.com","1199887766", "ORO");

        exec(c, "INSERT INTO productos(nombre,descripcion,precio) VALUES(?,?,?)",
                "Pizza Mozzarella",   "Pizza individual",      850.0);
        exec(c, "INSERT INTO productos(nombre,descripcion,precio) VALUES(?,?,?)",
                "Hamburguesa Clásica","Con papas fritas",     1200.0);
        exec(c, "INSERT INTO productos(nombre,descripcion,precio) VALUES(?,?,?)",
                "Empanadas x6",       "Carne a cuchillo",      950.0);
        exec(c, "INSERT INTO productos(nombre,descripcion,precio) VALUES(?,?,?)",
                "Sushi Roll x8",      "Salmón y palta",       1500.0);
        exec(c, "INSERT INTO productos(nombre,descripcion,precio) VALUES(?,?,?)",
                "Coca-Cola 500ml",    "Bebida",                250.0);

        exec(c, "INSERT INTO repartidores(nombre,vehiculo,disponible) VALUES(?,?,?)",
                "Carlos Ruiz",  "MOTO", 1);
        exec(c, "INSERT INTO repartidores(nombre,vehiculo,disponible) VALUES(?,?,?)",
                "Sofía Torres", "BICI", 1);
        exec(c, "INSERT INTO repartidores(nombre,vehiculo,disponible) VALUES(?,?,?)",
                "Diego Vargas", "AUTO", 1);

        System.out.println("Datos de prueba cargados.");
    }

    // ─── Usuarios ────────────────────────────────────────────────────────────

    public static List<Usuario> getUsuarios() {
        List<Usuario> list = new ArrayList<>();
        try (Connection c = connect();
             ResultSet rs = c.createStatement().executeQuery("SELECT * FROM usuarios")) {
            while (rs.next())
                list.add(new Usuario(rs.getLong("id"), rs.getString("nombre"),
                        rs.getString("email"), rs.getString("telefono"), getTierFromString(rs.getString("tier"))));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ─── Productos ───────────────────────────────────────────────────────────

    public static List<Producto> getProductos() {
        List<Producto> list = new ArrayList<>();
        try (Connection c = connect();
             ResultSet rs = c.createStatement().executeQuery("SELECT * FROM productos")) {
            while (rs.next())
                list.add(new Producto(rs.getLong("id"), rs.getString("nombre"),
                        rs.getString("descripcion"), rs.getDouble("precio")));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ─── Repartidores ────────────────────────────────────────────────────────

    public static List<Repartidor> getTodosLosRepartidores() {
        List<Repartidor> list = new ArrayList<>();
        try (Connection c = connect();
             ResultSet rs = c.createStatement().executeQuery("SELECT * FROM repartidores")) {
            while (rs.next())
                list.add(mapRepartidor(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static List<Repartidor> getRepartidoresDisponibles() {
        List<Repartidor> list = new ArrayList<>();
        try (Connection c = connect();
             ResultSet rs = c.createStatement().executeQuery(
                     "SELECT * FROM repartidores WHERE disponible = 1")) {
            while (rs.next())
                list.add(mapRepartidor(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static void updateRepartidor(Repartidor r) {
        exec(null, "UPDATE repartidores SET disponible=? WHERE id=?",
                r.isDisponible() ? 1 : 0, r.getId());
    }

    // ─── Pedidos ─────────────────────────────────────────────────────────────

    /** Guarda un pedido nuevo y retorna el id generado. */
    public static long savePedido(Pedido p) {
        long id = -1;
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO pedidos(estado,metodo_envio,costo_envio,usuario_id) VALUES(?,?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getEstado());
            ps.setString(2, p.getMetodoEnvio());
            ps.setDouble(3, p.getCostoEnvio());
            ps.setLong(4, p.getUsuario().getId());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                id = keys.getLong(1);
                // Insertar productos del pedido
                for (Producto prod : p.getProductos())
                    exec(c, "INSERT INTO pedido_productos VALUES(?,?)", id, prod.getId());
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return id;
    }

    /** Actualiza estado y repartidor de un pedido existente. */
    public static void updatePedido(Pedido p) {
        Long repId = p.getRepartidor() != null ? p.getRepartidor().getId() : null;
        exec(null, "UPDATE pedidos SET estado=?, repartidor_id=? WHERE id=?",
                p.getEstado(), repId, p.getId());
    }

    /** Obtiene pedidos filtrados por estado ("PENDIENTE", "EN_CAMINO", etc.). */
    public static List<Pedido> getPedidos(String estado) {
        List<Pedido> list = new ArrayList<>();
        String sql = "SELECT p.*, u.nombre as u_nombre, u.email as u_email, u.tier as u_tier "
                   + "FROM pedidos p JOIN usuarios u ON p.usuario_id = u.id "
                   + "WHERE p.estado = ?";
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, estado);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Pedido pedido = mapPedido(rs, c);
                list.add(pedido);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static List<Pedido> getPedidosPorUsuario(long usuarioId) {
        List<Pedido> list = new ArrayList<>();
        String sql = "SELECT p.*, u.nombre as u_nombre, u.email as u_email, u.tier as u_tier "
                   + "FROM pedidos p JOIN usuarios u ON p.usuario_id = u.id "
                   + "WHERE p.usuario_id = ? ORDER BY p.id DESC";
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapPedido(rs, c));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static List<Pedido> getPedidosPorRepartidor(long repartidorId) {
        List<Pedido> list = new ArrayList<>();
        String sql = "SELECT p.*, u.nombre as u_nombre, u.email as u_email, u.tier as u_tier "
                   + "FROM pedidos p JOIN usuarios u ON p.usuario_id = u.id "
                   + "WHERE p.repartidor_id = ? ORDER BY p.id DESC";
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, repartidorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapPedido(rs, c));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ─── Helpers privados ────────────────────────────────────────────────────

    private static Pedido mapPedido(ResultSet rs, Connection c) throws SQLException {
        Usuario u = new Usuario(rs.getLong("usuario_id"),
                rs.getString("u_nombre"), rs.getString("u_email"), null, getTierFromString(rs.getString("u_tier")));

        Repartidor rep = null;
        long repId = rs.getLong("repartidor_id");
        if (repId > 0) rep = getRepartidorById(c, repId);

        List<Producto> prods = getProductosDePedido(c, rs.getLong("id"));

        return new Pedido(
                rs.getLong("id"),
                rs.getString("estado"),
                rs.getString("metodo_envio"),
                rs.getDouble("costo_envio"),
                u, rep, prods);
    }

    private static Repartidor getRepartidorById(Connection c, long id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM repartidores WHERE id = ?")) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRepartidor(rs);
        }
        return null;
    }

    private static Repartidor mapRepartidor(ResultSet rs) throws SQLException {
        return new Repartidor(rs.getLong("id"), rs.getString("nombre"),
                getVehiculoFromString(rs.getString("vehiculo")), rs.getInt("disponible") == 1);
    }

    private static List<Producto> getProductosDePedido(Connection c, long pedidoId)
            throws SQLException {
        List<Producto> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT pr.* FROM productos pr "
                + "JOIN pedido_productos pp ON pr.id = pp.producto_id "
                + "WHERE pp.pedido_id = ?")) {
            ps.setLong(1, pedidoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(new Producto(rs.getLong("id"), rs.getString("nombre"),
                        rs.getString("descripcion"), rs.getDouble("precio")));
        }
        return list;
    }

    /** Ejecuta un INSERT/UPDATE/DELETE con parámetros. */
    private static void exec(Connection conn, String sql, Object... params) {
        boolean ownsConn = (conn == null);
        try {
            Connection c = ownsConn ? connect() : conn;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++)
                    ps.setObject(i + 1, params[i]);
                ps.executeUpdate();
            } finally {
                if (ownsConn) c.close();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static int count(Connection c, String tabla) throws SQLException {
        ResultSet rs = c.createStatement()
                .executeQuery("SELECT COUNT(*) FROM " + tabla);
        return rs.next() ? rs.getInt(1) : 0;
    }

    public static TierUsuario getTierFromString(String tierStr) {
        if (tierStr == null) return new TierNormal();
        switch (tierStr.toUpperCase()) {
            case "PLATA": return new TierPlata();
            case "ORO": return new TierOro();
            default: return new TierNormal();
        }
    }

    public static Vehiculo getVehiculoFromString(String vStr) {
        if (vStr == null) return new Moto();
        switch (vStr.toUpperCase()) {
            case "BICI": return new Bici();
            case "AUTO": return new Auto();
            case "MOTO":
            default: return new Moto();
        }
    }
}
