package delivery.envio;

/**
 * Registra los métodos de envío base del sistema.
 */
public class ConfiguradorEnvios {
    public static void configurar() {
        GestorEnvios.registrar(new EnvioMoto());
        GestorEnvios.registrar(new EnvioBici());
        GestorEnvios.registrar(new EnvioAuto());
    }
}
