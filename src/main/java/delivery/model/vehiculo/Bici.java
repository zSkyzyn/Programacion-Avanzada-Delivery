package delivery.model.vehiculo;

public class Bici extends Vehiculo {
    @Override
    public String getNombre() {
        return "BICI";
    }

    @Override
    public boolean puedeLlevar(String metodoEnvio) {
        return metodoEnvio != null && metodoEnvio.equalsIgnoreCase("Bici");
    }
}
