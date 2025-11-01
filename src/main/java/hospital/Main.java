package hospital;

import Presentation.Views.LoginView;
import javax.swing.*;

/**
 * Clase principal que inicia la aplicación del sistema hospitalario.
 * Muestra la ventana de login como punto de entrada.
 */
public class Main {
    public static void main(String[] args) {
        // Usar el EDT (Event Dispatch Thread) para la GUI de Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Configurar Look and Feel del sistema operativo
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Si falla, usar el Look and Feel por defecto
                e.printStackTrace();
            }

            // Crear y mostrar la ventana de login
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
}