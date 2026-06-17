package delivery.acciones;

import delivery.model.Pedido;
import delivery.model.Repartidor;

/** Simula una notificación al cliente (email, push, etc.). */
public class NotificarCliente implements Accion {
    private final String mensaje;

    public NotificarCliente(String mensaje) { 
        this.mensaje = mensaje; 
    }

    @Override
    public void ejecutar(Pedido pedido, Repartidor rep) {
        System.out.println("  [Notif.]     → " + pedido.getUsuario().getNombre()
                + ": pedido #" + pedido.getId() + " " + mensaje + " " + rep.getNombre());
    }
}
