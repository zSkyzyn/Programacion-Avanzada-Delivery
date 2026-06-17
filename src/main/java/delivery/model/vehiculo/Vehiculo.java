package delivery.model.vehiculo;

public abstract class Vehiculo {
    public abstract String getNombre();
    public abstract boolean puedeLlevar(String metodoEnvio);

    @Override
    public String toString() {
        return getNombre();
    }
}
