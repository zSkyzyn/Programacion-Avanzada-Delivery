package delivery.gui;

import delivery.Database;
import delivery.envio.GestorEnvios;
import delivery.envio.MetodoEnvio;
import delivery.model.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel donde el cliente arma y envía su pedido.
 * Los métodos de envío se cargan de GestorEnvios.todos() — OCP.
 */
public class PanelPedidos extends JPanel {

    private JComboBox<Usuario>    comboUsuarios;
    private JComboBox<MetodoEnvio> comboEnvio;
    private DefaultListModel<Producto> modeloProductos;
    private JList<Producto> listaProductos;
    private JLabel lblTotal;
    private JLabel lblTierInfo;
    private JTextArea areaResumen;
    private JTextArea areaHistorialUsuario;
    private JButton btnGenerarPedido;

    public PanelPedidos() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        construirUI();
    }

    private void construirUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(new TitledBorder("Cliente"));
        top.add(new JLabel("Usuario:"));
        comboUsuarios = new JComboBox<>();
        comboUsuarios.setPreferredSize(new Dimension(220, 25));
        top.add(comboUsuarios);
        lblTierInfo = new JLabel("");
        lblTierInfo.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblTierInfo.setForeground(new Color(100, 100, 100));
        top.add(lblTierInfo);
        add(top, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(1, 3, 10, 0));

        JPanel pProductos = new JPanel(new BorderLayout());
        pProductos.setBorder(new TitledBorder("Productos  (Ctrl+clic = múltiples)"));
        modeloProductos = new DefaultListModel<>();
        listaProductos = new JList<>(modeloProductos);
        listaProductos.setFont(new Font("Monospaced", Font.PLAIN, 12));
        pProductos.add(new JScrollPane(listaProductos));
        centro.add(pProductos);

        JPanel pEnvio = new JPanel(new GridBagLayout());
        pEnvio.setBorder(new TitledBorder("Método de Envío"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6); g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0; pEnvio.add(new JLabel("Envío:"), g);
        g.gridx = 1; comboEnvio = new JComboBox<>();
        pEnvio.add(comboEnvio, g);

        g.gridx = 0; g.gridy = 1; pEnvio.add(new JLabel("Total estimado:"), g);
        g.gridx = 1; lblTotal = new JLabel("$0");
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 16));
        pEnvio.add(lblTotal, g);
        centro.add(pEnvio);
        
        JPanel pHistorial = new JPanel(new BorderLayout());
        pHistorial.setBorder(new TitledBorder("Historial de mis Pedidos"));
        areaHistorialUsuario = new JTextArea(10, 30);
        areaHistorialUsuario.setEditable(false);
        areaHistorialUsuario.setFont(new Font("Monospaced", Font.PLAIN, 11));
        pHistorial.add(new JScrollPane(areaHistorialUsuario), BorderLayout.CENTER);
        centro.add(pHistorial);
        
        add(centro, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        btnGenerarPedido = new JButton("Generar Pedido");
        btnGenerarPedido.setFont(new Font("SansSerif", Font.BOLD, 13));
        bottom.add(btnGenerarPedido, BorderLayout.NORTH);
        areaResumen = new JTextArea(4, 0);
        areaResumen.setEditable(false);
        areaResumen.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaResumen.setBorder(new TitledBorder("Último pedido generado"));
        bottom.add(new JScrollPane(areaResumen), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    
    public JComboBox<Usuario> getComboUsuarios() { return comboUsuarios; }
    public JComboBox<MetodoEnvio> getComboEnvio() { return comboEnvio; }
    public DefaultListModel<Producto> getModeloProductos() { return modeloProductos; }
    public JList<Producto> getListaProductos() { return listaProductos; }
    public JButton getBtnGenerarPedido() { return btnGenerarPedido; }
    public JLabel getLblTotal() { return lblTotal; }
    public JLabel getLblTierInfo() { return lblTierInfo; }
    public JTextArea getAreaResumen() { return areaResumen; }
    public JTextArea getAreaHistorialUsuario() { return areaHistorialUsuario; }
}
