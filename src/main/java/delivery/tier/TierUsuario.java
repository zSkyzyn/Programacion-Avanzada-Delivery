package delivery.tier;

public interface TierUsuario {
    String getNombre();
    double calcularDescuentoSubtotal(double subtotal);
    double calcularDescuentoEnvio(double costoEnvio);
}
