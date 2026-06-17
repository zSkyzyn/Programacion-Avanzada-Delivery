package delivery.model.vehiculo;

public class Moto extends Vehiculo {
    @Override
    public String getNombre() {
        return "MOTO";
    }

    @Override
    public boolean puedeLlevar(String metodoEnvio) {
        return metodoEnvio != null && metodoEnvio.equalsIgnoreCase("Moto");
    }
}
