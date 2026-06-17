package delivery.tier;

public class TierPlata implements TierUsuario{
    @Override public String getNombre() {return "Plata";}
    @Override public double calcularDescuentoSubtotal(double subtotal) {return subtotal * 0.05;}
    @Override public double calcularDescuentoEnvio(double costoEnvio) {return costoEnvio * 0.20;}
}

