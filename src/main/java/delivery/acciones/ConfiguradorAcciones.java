package delivery.acciones;


public class ConfiguradorAcciones {

    
    public static void configurar() {

        GestorPedido.registrarAccion("ACEPTACION", new CambiarEstado("EN_CAMINO"));
        
        GestorPedido.registrarAccion("ACEPTACION", new AsignarRepartidor());
        
        GestorPedido.registrarAccion("ACEPTACION", new NotificarCliente("aceptado por"));


        GestorPedido.registrarAccion("ENTREGA", new CambiarEstado("ENTREGADO"));
        
        GestorPedido.registrarAccion("ENTREGA", new LiberarRepartidor());
        
        GestorPedido.registrarAccion("ENTREGA", new NotificarCliente("entregado — ¡que lo disfrute!"));
    }
}
