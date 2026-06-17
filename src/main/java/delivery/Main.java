package delivery;

import delivery.gui.MainFrame;
import javax.swing.SwingUtilities;


public class Main {

    public static void main(String[] args) {
        Database.init();                           // Crea tablas y datos de prueba
        delivery.acciones.ConfiguradorAcciones.configurar(); // Configura las acciones (Principio OCP)
        delivery.envio.ConfiguradorEnvios.configurar();      // Configura los métodos de envío (Principio OCP)
        SwingUtilities.invokeLater(MainFrame::new); // Abre la GUI
    }
}
