package delivery.envio;


public interface MetodoEnvio {

    String getNombre();
    double calcularCosto(double subtotal);
}
