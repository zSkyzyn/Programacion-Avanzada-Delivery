package delivery.acciones;

import delivery.model.Pedido;
import delivery.model.Repartidor;

public interface Accion {
    void ejecutar(Pedido pedido, Repartidor repartidor);
}
