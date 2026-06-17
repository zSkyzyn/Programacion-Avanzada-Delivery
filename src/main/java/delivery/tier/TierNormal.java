package delivery.tier;

public class TierNormal implements TierUsuario{
    @Override public String getNombre() {return "Normal";}
    @Override public double calcularDescuentoSubtotal(double subtotal) {return 0;}
    @Override public double calcularDescuentoEnvio(double costoEnvio) {return 0;}
}

