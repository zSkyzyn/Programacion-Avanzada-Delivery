package delivery.acciones;

import delivery.model.Pedido;
import delivery.model.Repartidor;

/** Cambia el estado del pedido al valor indicado. */
public class CambiarEstado implements Accion {
    private final String nuevoEstado;

    public CambiarEstado(String nuevoEstado) { 
        this.nuevoEstado = nuevoEstado; 
    }

    @Override
    public void ejecutar(Pedido pedido, Repartidor rep) {
        pedido.setEstado(nuevoEstado);
        System.out.println("  [Estado]     → " + nuevoEstado);
    }
}
