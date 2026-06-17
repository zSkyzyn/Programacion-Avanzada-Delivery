package delivery.envio;

public class EnvioAuto implements MetodoEnvio {
    @Override public String getNombre() { return "Auto"; }
    @Override public double calcularCosto(double subtotal) { return 250 + subtotal * 0.08; }
    @Override public String toString() { return "Auto  ($250 + 8%)"; }
}
