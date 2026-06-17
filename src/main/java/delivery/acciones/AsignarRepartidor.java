package delivery.acciones;

import delivery.model.Pedido;
import delivery.model.Repartidor;

/** Asigna el repartidor al pedido y lo marca como no disponible. */
public class AsignarRepartidor implements Accion {
    @Override
    public void ejecutar(Pedido pedido, Repartidor rep) {
        pedido.setRepartidor(rep);
        rep.setDisponible(false);
        System.out.println("  [Repartidor] → " + rep.getNombre() + " asignado.");
    }
}
