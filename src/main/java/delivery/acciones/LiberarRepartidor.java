package delivery.acciones;

import delivery.model.Pedido;
import delivery.model.Repartidor;

/** Libera al repartidor para nuevos pedidos una vez entregado. */
public class LiberarRepartidor implements Accion {
    @Override
    public void ejecutar(Pedido pedido, Repartidor rep) {
        rep.setDisponible(true);
        System.out.println("  [Repartidor] → " + rep.getNombre() + " disponible nuevamente.");
    }
}
