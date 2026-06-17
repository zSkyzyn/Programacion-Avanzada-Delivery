package delivery.gui;

import delivery.Database;
import delivery.acciones.GestorPedido;
import delivery.model.Pedido;
import delivery.model.Repartidor;

import javax.swing.*;
import java.util.List;

public class ControladorRepartidor {

    private final PanelRepartidor vista;

    public ControladorRepartidor(PanelRepartidor vista) {
        this.vista = vista;
        inicializarEventos();
        recargar();
    }

    private void inicializarEventos() {
        vista.getComboRepartidores().addActionListener(e -> filtrarPendientesYEnCamino());
        vista.getBtnAceptar().addActionListener(e -> aceptar());
        vista.getBtnEntregar().addActionListener(e -> confirmarEntrega());
    }

    public void recargar() {
        Repartidor current = (Repartidor) vista.getComboRepartidores().getSelectedItem();
        long currentId = current != null ? current.getId() : -1;
        
        vista.getComboRepartidores().removeAllItems();
        for (Repartidor r : Database.getTodosLosRepartidores()) {
            vista.getComboRepartidores().addItem(r);
            if (r.getId() == currentId) {
                vista.getComboRepartidores().setSelectedItem(r);
            }
        }
        filtrarPendientesYEnCamino();
    }

    private void filtrarPendientesYEnCamino() {
        vista.getModeloPendientes().clear();
        vista.getModeloEnCamino().clear();
        if (vista.getAreaHistorialRepartidor() != null) vista.getAreaHistorialRepartidor().setText("");
        
        Repartidor rep = (Repartidor) vista.getComboRepartidores().getSelectedItem();
        if (rep == null) return;

        if (rep.isDisponible()) {
            for (Pedido p : Database.getPedidos("PENDIENTE")) {
                if (rep.getVehiculo().puedeLlevar(p.getMetodoEnvio())) {
                    vista.getModeloPendientes().addElement(p);
                }
            }
        }

        for (Pedido p : Database.getPedidos("EN_CAMINO")) {
            if (p.getRepartidor() != null && p.getRepartidor().getId() == rep.getId()) {
                vista.getModeloEnCamino().addElement(p);
            }
        }

        if (vista.getAreaHistorialRepartidor() != null) {
            List<Pedido> historial = Database.getPedidosPorRepartidor(rep.getId());
            if (historial.isEmpty()) {
                vista.getAreaHistorialRepartidor().append("No hay pedidos en el historial.");
            } else {
                for (Pedido p : historial) {
                    vista.getAreaHistorialRepartidor().append(String.format("Pedido #%d | %s | $%.0f\n", p.getId(), p.getEstado(), p.getTotal()));
                }
            }
        }
    }

    private void aceptar() {
        Pedido pedido = vista.getListaPendientes().getSelectedValue();
        Repartidor rep = (Repartidor) vista.getComboRepartidores().getSelectedItem();

        if (pedido == null) { aviso("Seleccioná un pedido pendiente."); return; }
        if (rep == null)    { aviso("No hay repartidores disponibles."); return; }
        if (!rep.isDisponible()) { aviso("Este repartidor ya no está disponible."); return; }

        GestorPedido.ejecutarEvento("ACEPTACION", pedido, rep);   // ← cadena OCP
        Database.updatePedido(pedido);
        Database.updateRepartidor(rep);

        log("ACEPTACIÓN  Pedido #" + pedido.getId() + " → EN_CAMINO | " + rep.getNombre());
        recargar();
        JOptionPane.showMessageDialog(vista,
                "Pedido #" + pedido.getId() + " aceptado → EN_CAMINO",
                "Aceptado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void confirmarEntrega() {
        Pedido pedido = vista.getListaEnCamino().getSelectedValue();
        if (pedido == null) { aviso("Seleccioná un pedido en camino."); return; }

        Repartidor rep = pedido.getRepartidor();
        if (rep == null) { aviso("El pedido no tiene repartidor asignado."); return; }

        GestorPedido.ejecutarEvento("ENTREGA", pedido, rep);      // ← cadena OCP
        Database.updatePedido(pedido);
        Database.updateRepartidor(rep);

        log("ENTREGA     Pedido #" + pedido.getId() + " → ENTREGADO | " + pedido.getUsuario().getNombre());
        recargar();
        JOptionPane.showMessageDialog(vista,
                "Pedido #" + pedido.getId() + " entregado.\n"
                + rep.getNombre() + " está disponible nuevamente.",
                "Entregado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void log(String texto) {
        vista.getAreaLog().append(texto + "\n────────────────────────────────────\n");
    }

    private void aviso(String msg) {
        JOptionPane.showMessageDialog(vista, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}
