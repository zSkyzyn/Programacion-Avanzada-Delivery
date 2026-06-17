package delivery.model;

public class Producto {
    private long id;
    private String nombre, descripcion;
    private double precio;

    public Producto(long id, String nombre, String descripcion, double precio) {
        this.id = id; this.nombre = nombre;
        this.descripcion = descripcion; this.precio = precio;
    }

    public long getId()          { return id; }
    public String getNombre()    { return nombre; }
    public double getPrecio()    { return precio; }

    @Override public String toString() {
        return nombre + "  $" + String.format("%.0f", precio);
    }
}
