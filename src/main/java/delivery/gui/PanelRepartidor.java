package delivery.gui;

import delivery.Database;
import delivery.acciones.GestorPedido;
import delivery.model.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

/**
 * Panel del repartidor.
 * Flujo: PENDIENTE → [Aceptar] → EN_CAMINO → [Confirmar Entrega] → ENTREGADO
 */
public class PanelRepartidor extends JPanel {

    private JComboBox<Repartidor>  comboRepartidores;
    private DefaultListModel<Pedido> modeloPendientes = new DefaultListModel<>();
    private DefaultListModel<Pedido> modeloEnCamino   = new DefaultListModel<>();
    private JList<Pedido> listaPendientes;
    private JList<Pedido> listaEnCamino;
    private JTextArea areaLog;
    private JTextArea areaHistorialRepartidor;
    private JButton btnAceptar;
    private JButton btnEntregar;

    public PanelRepartidor() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        construirUI();
    }

    private void construirUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(new TitledBorder("Repartidor activo"));
        top.add(new JLabel("Repartidor:"));
        comboRepartidores = new JComboBox<>();
        comboRepartidores.setPreferredSize(new Dimension(240, 25));
        top.add(comboRepartidores);
        add(top, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(1, 3, 10, 0));

        JPanel pPend = new JPanel(new BorderLayout(4, 4));
        pPend.setBorder(new TitledBorder("Pedidos PENDIENTES"));
        listaPendientes = new JList<>(modeloPendientes);
        listaPendientes.setFont(new Font("Monospaced", Font.PLAIN, 11));
        pPend.add(new JScrollPane(listaPendientes), BorderLayout.CENTER);
        btnAceptar = crearBoton("Aceptar", new Color(52, 152, 219));
        pPend.add(btnAceptar, BorderLayout.SOUTH);
        centro.add(pPend);

        JPanel pCamino = new JPanel(new BorderLayout(4, 4));
        pCamino.setBorder(new TitledBorder("Pedidos EN CAMINO"));
        listaEnCamino = new JList<>(modeloEnCamino);
        listaEnCamino.setFont(new Font("Monospaced", Font.PLAIN, 11));
        pCamino.add(new JScrollPane(listaEnCamino), BorderLayout.CENTER);
        btnEntregar = crearBoton("Confirmar Entrega", new Color(39, 174, 96));
        pCamino.add(btnEntregar, BorderLayout.SOUTH);
        centro.add(pCamino);

        JPanel pHistorial = new JPanel(new BorderLayout());
        pHistorial.setBorder(new TitledBorder("Historial del Repartidor"));
        areaHistorialRepartidor = new JTextArea(10, 30);
        areaHistorialRepartidor.setEditable(false);
        areaHistorialRepartidor.setFont(new Font("Monospaced", Font.PLAIN, 11));
        pHistorial.add(new JScrollPane(areaHistorialRepartidor), BorderLayout.CENTER);
        centro.add(pHistorial);

        add(centro, BorderLayout.CENTER);

        areaLog = new JTextArea(4, 0);
        areaLog.setEditable(false);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JPanel pLog = new JPanel(new BorderLayout());
        pLog.setBorder(new TitledBorder("Log de acciones"));
        pLog.add(new JScrollPane(areaLog));
        add(pLog, BorderLayout.SOUTH);
    }


    public JComboBox<Repartidor> getComboRepartidores() { return comboRepartidores; }
    public DefaultListModel<Pedido> getModeloPendientes() { return modeloPendientes; }
    public DefaultListModel<Pedido> getModeloEnCamino() { return modeloEnCamino; }
    public JList<Pedido> getListaPendientes() { return listaPendientes; }
    public JList<Pedido> getListaEnCamino() { return listaEnCamino; }
    public JTextArea getAreaLog() { return areaLog; }
    public JTextArea getAreaHistorialRepartidor() { return areaHistorialRepartidor; }
    public JButton getBtnAceptar() { return btnAceptar; }
    public JButton getBtnEntregar() { return btnEntregar; }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        return btn;
    }
}
