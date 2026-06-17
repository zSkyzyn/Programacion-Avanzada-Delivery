package delivery.gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Plataforma Delivery — TP3");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 550);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));

        PanelPedidos panelPedidos = new PanelPedidos();
        ControladorPedidos ctrlPedidos = new ControladorPedidos(panelPedidos);

        PanelRepartidor panelRepartidor = new PanelRepartidor();
        ControladorRepartidor ctrlRepartidor = new ControladorRepartidor(panelRepartidor);

        tabs.addTab("Nuevo Pedido",     panelPedidos);
        tabs.addTab("Panel Repartidor", panelRepartidor);

        // Recargar los controladores al cambiar de pestaña
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() == panelRepartidor) {
                ctrlRepartidor.recargar();
            } else if (tabs.getSelectedComponent() == panelPedidos) {
                ctrlPedidos.recargar();
            }
        });

        add(tabs);
        setVisible(true);
    }
}
