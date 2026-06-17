package delivery.envio;

public class EnvioMoto implements MetodoEnvio {
    @Override public String getNombre() { return "Moto"; }
    @Override public double calcularCosto(double subtotal) { return 150 + subtotal * 0.05; }
    @Override public String toString() { return "Moto  ($150 + 5%)"; }
}
