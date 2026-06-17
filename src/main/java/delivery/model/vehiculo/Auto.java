package delivery.model.vehiculo;

public class Auto extends Vehiculo {
    @Override
    public String getNombre() {
        return "AUTO";
    }

    @Override
    public boolean puedeLlevar(String metodoEnvio) {
        return metodoEnvio != null && metodoEnvio.equalsIgnoreCase("Auto");
    }
}
