package delivery.gui;

import delivery.Database;
import delivery.envio.GestorEnvios;
import delivery.envio.MetodoEnvio;
import delivery.model.Pedido;
import delivery.model.Producto;
import delivery.model.Usuario;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ControladorPedidos {

    private final PanelPedidos vista;

    public ControladorPedidos(PanelPedidos vista) {
        this.vista = vista;
        inicializarEventos();
        cargarDatos();
    }

    private void inicializarEventos() {
        vista.getComboUsuarios().addActionListener(e -> recalcular());
        vista.getComboEnvio().addActionListener(e -> recalcular());
        vista.getListaProductos().addListSelectionListener(e -> recalcular());
        vista.getBtnGenerarPedido().addActionListener(e -> generarPedido());
    }

    private void cargarDatos() {
        vista.getComboUsuarios().removeAllItems();
        Database.getUsuarios().forEach(u -> vista.getComboUsuarios().addItem(u));

        vista.getModeloProductos().clear();
        Database.getProductos().forEach(p -> vista.getModeloProductos().addElement(p));

        vista.getComboEnvio().removeAllItems();
        GestorEnvios.todos().forEach(m -> vista.getComboEnvio().addItem(m));
    }

    public void recargar() {
        recalcular();
    }

    private void recalcular() {
        Usuario u = (Usuario) vista.getComboUsuarios().getSelectedItem();
        if (u == null) return;

        // Mostrar info del tier
        if (u.getTier().getNombre().equals("NORMAL")) {
            vista.getLblTierInfo().setText("  (Sin beneficios)");
        } else if (u.getTier().getNombre().equals("PLATA")) {
            vista.getLblTierInfo().setText("  (Tier Plata: 10% de descuento en total)");
        } else if (u.getTier().getNombre().equals("ORO")) {
            vista.getLblTierInfo().setText("  (Tier Oro: Envío GRATIS)");
        }

        // Cargar historial
        vista.getAreaHistorialUsuario().setText("");
        List<Pedido> historial = Database.getPedidosPorUsuario(u.getId());
        if (historial.isEmpty()) {
            vista.getAreaHistorialUsuario().append("No hay pedidos previos.");
        } else {
            for (Pedido p : historial) {
                vista.getAreaHistorialUsuario().append(String.format("Pedido #%d | %s | $%.0f\n", p.getId(), p.getEstado(), p.getTotal()));
            }
        }

        MetodoEnvio m = (MetodoEnvio) vista.getComboEnvio().getSelectedItem();
        if (m == null) return;
        
        double sub = vista.getListaProductos().getSelectedValuesList()
                .stream().mapToDouble(Producto::getPrecio).sum();
        
        double costoEnvio = m.calcularCosto(sub);
        double descSub = u.getTier().calcularDescuentoSubtotal(sub);
        double descEnvio = u.getTier().calcularDescuentoEnvio(costoEnvio);
        
        double total = (sub - descSub) + (costoEnvio - descEnvio);
        double totalDesc = descSub + descEnvio;
        
        if (totalDesc > 0) {
            vista.getLblTotal().setText(String.format("$%.0f (-$%.0f desc)", total, totalDesc));
        } else {
            vista.getLblTotal().setText("$" + String.format("%.0f", total));
        }
    }

    private void generarPedido() {
        Usuario usuario = (Usuario) vista.getComboUsuarios().getSelectedItem();
        MetodoEnvio metodo = (MetodoEnvio) vista.getComboEnvio().getSelectedItem();
        List<Producto> seleccionados = vista.getListaProductos().getSelectedValuesList();

        if (usuario == null || metodo == null || seleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "Seleccioná usuario, al menos un producto y el método de envío.",
                    "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double sub = seleccionados.stream().mapToDouble(Producto::getPrecio).sum();
        double costoEnvio = metodo.calcularCosto(sub);

        double descSub = usuario.getTier().calcularDescuentoSubtotal(sub);
        double descEnvio = usuario.getTier().calcularDescuentoEnvio(costoEnvio);
        double total = (sub - descSub) + (costoEnvio - descEnvio);

        Pedido pedido = new Pedido(0, "PENDIENTE", metodo.getNombre(),
                costoEnvio, usuario, null, new ArrayList<>(seleccionados));

        long id = Database.savePedido(pedido);

        vista.getAreaResumen().setText(String.format(
                "Pedido #%d creado\nCliente  : %s [%s]\nProductos: %s\nEnvío    : %s → $%.0f\nDescuento: -$%.0f\nTOTAL    : $%.0f",
                id, usuario.getNombre(), usuario.getTier().getNombre(),
                seleccionados.stream().map(Producto::getNombre)
                             .reduce((a, b) -> a + ", " + b).orElse(""),
                metodo.getNombre(), costoEnvio, (descSub + descEnvio), total));

        vista.getListaProductos().clearSelection();
        recalcular();
    }
}
