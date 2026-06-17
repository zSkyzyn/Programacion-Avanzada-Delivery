package delivery.envio;

public class EnvioBici implements MetodoEnvio {
    @Override public String getNombre() { return "Bici"; }
    @Override public double calcularCosto(double subtotal) { return 80; }
    @Override public String toString() { return "Bici  ($80 fijo)"; }
}
