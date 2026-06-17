package delivery.tier;

public class TierOro implements TierUsuario{
    @Override public String getNombre() {return "Oro";}
    @Override public double calcularDescuentoSubtotal(double subtotal) {return subtotal * 0.15;}
    @Override public double calcularDescuentoEnvio(double costoEnvio) {return costoEnvio;}
}
