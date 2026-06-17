# Plataforma Delivery — Documentación Técnica

**Materia:** Programación Avanzada  
**Trabajo Práctico:** TP-3  
**Principio demostrado:** Open/Closed Principle (OCP) — SOLID  
**Alumnos:** Astie Agustin 114754, Lopez Luciano 114434  


## 1. Descripción general del sistema

La plataforma es una aplicación de escritorio Java que simula el ciclo completo de un servicio de delivery. Permite a un **cliente** generar pedidos eligiendo productos y método de envío, y a un **repartidor** aceptar esos pedidos y confirmar su entrega.

El objetivo es demostrar el **Principio Open/Closed (OCP)**: el sistema está diseñado para que agregar nuevas funcionalidades (un nuevo vehículo, una nueva acción al aceptar un pedido) no requiera modificar el código que ya existe y funciona.

---

## 2. Tecnologías utilizadas

| Tecnología | Rol en el sistema |
|---|---|
| **Java 17+** | Lenguaje de programación |
| **SQLite (via JDBC)** | Base de datos embebida en un archivo `.db` local |
| **sqlite-jdbc 3.45** | Driver JDBC para conectar Java con SQLite |
| **Java Swing** | Interfaz gráfica de usuario |
| **Maven** | Gestión de dependencias y compilación |
| **PlantUML** | Generación de diagramas (ER y flujo) |

Se eligió **SQLite sin ORM** deliberadamente para que el código sea simple y legible.

---

## 3. Arquitectura del proyecto

```
src/main/java/delivery/
│
├── Main.java                    ← Punto de entrada (3 líneas)
├── Database.java                ← Toda la persistencia (JDBC directo)
│
├── model/                       ← Entidades del dominio
│   ├── Usuario.java
│   ├── Producto.java
│   ├── Repartidor.java
│   ├── Pedido.java
│   └── vehiculo/                ← OCP: Tipos de vehículos
│       ├── Vehiculo.java
│       ├── Moto.java
│       ├── Bici.java
│       └── Auto.java
│
├── tier/                        ← OCP: Beneficios y descuentos por nivel
│   ├── TierUsuario.java         (interfaz)
│   ├── TierNormal.java
│   ├── TierPlata.java
│   └── TierOro.java
│
├── envio/                       ← OCP: Métodos de envío extensibles
│   ├── MetodoEnvio.java         (interfaz)
│   ├── GestorEnvios.java        (registro dinámico de envíos)
│   ├── EnvioMoto.java
│   ├── EnvioBici.java
│   └── EnvioAuto.java
│
├── acciones/                    ← OCP: Acciones extensibles al aceptar/entregar
│   ├── Accion.java              (interfaz única)
│   ├── GestorPedido.java        (gestor genérico de eventos)
│   └── CambiarEstado.java, etc. (implementaciones públicas)
│
└── gui/                         ← Interfaz gráfica Swing (Patrón MVC)
    ├── MainFrame.java
    ├── PanelPedidos.java        (Vista)
    ├── ControladorPedidos.java  (Controlador)
    ├── PanelRepartidor.java     (Vista)
    └── ControladorRepartidor.java (Controlador)
```

---

## 4. Modelo de datos — Base de datos SQLite

La base de datos se crea automáticamente al iniciar la aplicación (archivo `delivery.db`). Contiene 5 tablas:

```sql
-- El cliente que genera pedidos
CREATE TABLE usuarios (
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre   TEXT NOT NULL,
    email    TEXT NOT NULL UNIQUE,
    telefono TEXT,
    tier     TEXT NOT NULL DEFAULT 'NORMAL'
);

-- Catálogo de productos disponibles
CREATE TABLE productos (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre      TEXT NOT NULL,
    descripcion TEXT,
    precio      REAL NOT NULL
);

-- Personal de reparto y su vehículo
CREATE TABLE repartidores (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre     TEXT NOT NULL,
    vehiculo   TEXT NOT NULL,       -- "MOTO", "BICI", "AUTO"
    disponible INTEGER NOT NULL     -- 1 = libre, 0 = ocupado
);

-- Pedido: entidad central que vincula todo
CREATE TABLE pedidos (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    estado        TEXT NOT NULL,    -- PENDIENTE | EN_CAMINO | ENTREGADO
    metodo_envio  TEXT NOT NULL,    -- "Moto" | "Bici" | "Auto"
    costo_envio   REAL NOT NULL,
    usuario_id    INTEGER NOT NULL REFERENCES usuarios(id),
    repartidor_id INTEGER REFERENCES repartidores(id)   -- NULL hasta ser aceptado
);

-- Tabla intermedia: un pedido puede tener muchos productos
CREATE TABLE pedido_productos (
    pedido_id   INTEGER REFERENCES pedidos(id),
    producto_id INTEGER REFERENCES productos(id),
    PRIMARY KEY (pedido_id, producto_id)
);
```

### Relaciones entre tablas

```
usuarios ──< pedidos >── repartidores
              │
              ∨
        pedido_productos
              │
              ∨
           productos
```

- Un **usuario** puede tener muchos pedidos.
- Un **repartidor** puede atender muchos pedidos (pero uno a la vez, según `disponible`).
- Un **pedido** puede contener muchos **productos** (relación Many-to-Many resuelta con `pedido_productos`).
- El campo `repartidor_id` en `pedidos` es `NULL` mientras el pedido está `PENDIENTE` y se llena cuando es aceptado.

## 5. Ciclo de vida de un pedido

```
Estado inicial → PENDIENTE
                    │
              [Repartidor acepta]
              GestorPedido.ejecutarEvento("ACEPTACION", ...)
                    │
                    ▼
                EN_CAMINO
                    │
              [Repartidor confirma entrega]
              GestorPedido.ejecutarEvento("ENTREGA", ...)
                    │
                    ▼
                ENTREGADO
```

**Paso 1 — El cliente crea el pedido:**
Selecciona usuario, productos y método de envío. El sistema calcula el costo con `metodo.calcularCosto(subtotal)`. Al confirmar se llama a `Database.savePedido()` y queda en estado `PENDIENTE`.

**Paso 2 — El repartidor acepta:**
Ve los pedidos `PENDIENTE` en la columna izquierda. Al hacer clic en "Aceptar", se ejecuta el evento `"ACEPTACION"` a través de `GestorPedido`, que cambia el estado, asigna el repartidor y notifica al cliente. Luego persiste los cambios.

**Paso 3 — El repartidor confirma la entrega:**
Ve los pedidos `EN_CAMINO` en la columna derecha. Al hacer clic en "Confirmar Entrega", se ejecuta el evento `"ENTREGA"`, que cambia el estado a `ENTREGADO`, libera al repartidor y notifica al cliente.

---

