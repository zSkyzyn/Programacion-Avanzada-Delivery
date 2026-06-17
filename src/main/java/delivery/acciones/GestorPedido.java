package delivery.acciones;

import delivery.model.Pedido;
import delivery.model.Repartidor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GestorPedido {

    // Diccionario (Map) donde la CLAVE es el nombre del evento (ej. "ACEPTACION") 
    private static final Map<String, List<Accion>> registroDeAccionesPorEvento = new HashMap<>();

    /**
     * Registra una acción para que se ejecute cuando ocurra un evento específico.
     * 
     * @param nombreEvento El identificador del evento (ej. "ACEPTACION").
     * @param nuevaAccion La tarea a realizar (ej. cambiar el estado, notificar).
     */
    public static void registrarAccion(String nombreEvento, Accion nuevaAccion) {  
        registroDeAccionesPorEvento
            .computeIfAbsent(nombreEvento, k -> new ArrayList<>())
            .add(nuevaAccion);
    }

    /**
     * Dispara un evento, ejecutando en orden todas las acciones registradas para el mismo.
     * 
     * @param nombreEvento El evento que acaba de ocurrir.
     * @param pedido El pedido afectado.
     * @param repartidor El repartidor involucrado en el evento.
     */
    public static void ejecutarEvento(String nombreEvento, Pedido pedido, Repartidor repartidor) {
        System.out.println("--- Ejecutando cadena de acciones para el evento: [" + nombreEvento + "] ---");
        
   
        List<Accion> cadenaDeAcciones = registroDeAccionesPorEvento.getOrDefault(nombreEvento, new ArrayList<>());
        
        for (Accion accion : cadenaDeAcciones) {
            accion.ejecutar(pedido, repartidor);
        }
    }
}
