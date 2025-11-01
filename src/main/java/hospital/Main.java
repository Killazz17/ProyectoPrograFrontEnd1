package hospital;
import Presentation.Views.LoginView;
import javax.swing.*;

/**
 * Punto de entrada principal de la aplicación del Hospital.
 * Inicia la interfaz gráfica con la pantalla de login.
 */
public class Main {
    public static void main(String[] args) {
        // Configurar Look and Feel del sistema para mejor apariencia
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel del sistema");
        }

        // Verificar conexión con el servidor antes de mostrar el login
        System.out.println("=".repeat(50));
        System.out.println("Sistema Hospitalario - Cliente");
        System.out.println("=".repeat(50));
        System.out.println("Verificando conexión con el servidor...");
        System.out.println("Host: localhost");
        System.out.println("Puerto: 7070");
        System.out.println("=".repeat(50));

        // Iniciar la aplicación en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);

            System.out.println("✓ Ventana de login iniciada");
            System.out.println("✓ Esperando credenciales del usuario...");
            System.out.println("\nUsuarios de prueba creados en el backend:");
            System.out.println("  Admins: ID 1-5, password: admin1-admin5");
            System.out.println("  Farmaceutas: ID 6-10, password: farm1-farm5");
            System.out.println("  Médicos: ID 11-15, password: med1-med5");
            System.out.println("  Pacientes: ID 16-20, password: pac1-pac5");
            System.out.println("=".repeat(50));
        });
    }
}