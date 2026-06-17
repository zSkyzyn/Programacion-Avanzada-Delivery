package delivery.envio;

import java.util.ArrayList;
import java.util.List;


public class GestorEnvios {

    private static final List<MetodoEnvio> REGISTRO = new ArrayList<>();

    public static void registrar(MetodoEnvio metodo) {
        REGISTRO.add(metodo);
    }

    /** Lista de todos los métodos disponibles en el sistema. */
    public static List<MetodoEnvio> todos() {
        return new ArrayList<>(REGISTRO);
    }
}
