package delivery.model;

import delivery.model.vehiculo.Vehiculo;

public class Repartidor {
    private long id;
    private String nombre;
    private Vehiculo vehiculo;
    private boolean disponible;

    public Repartidor(long id, String nombre, Vehiculo vehiculo, boolean disponible) {
        this.id = id; this.nombre = nombre;
        this.vehiculo = vehiculo; this.disponible = disponible;
    }

    public long getId()           { return id; }
    public String getNombre()     { return nombre; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean d) { this.disponible = d; }

    @Override public String toString() {
        return nombre + " [" + vehiculo.getNombre() + "]";
    }
}
