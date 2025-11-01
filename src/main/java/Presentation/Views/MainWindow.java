package Presentation.Views;

import Domain.Dtos.LoginResponseDto;
import javax.swing.*;

/**
 * Ventana principal del sistema hospitalario.
 * Se abre después de un login exitoso y configura las pestañas
 * según el rol del usuario autenticado.
 */
public class MainWindow extends JFrame {
    private JPanel ContentPanel;
    private JTabbedPane MainTabPanel;
    private JButton LogoutButton;

    private final LoginResponseDto usuario;

    // Constructor que recibe al usuario autenticado
    public MainWindow(LoginResponseDto usuario) {
        this.usuario = usuario;

        setupFrame();
        setupTabs();
        setupListeners();
    }

    private void setupFrame() {
        setContentPane(ContentPanel);
        setTitle("Sistema Hospital - " + usuario.getNombre() + " (" + usuario.getRol() + ")");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void setupTabs() {
        String rol = usuario.getRol();

        if (rol == null) return;

        switch (rol.toUpperCase()) {
            case "ADMIN":
                setupAdminTabs();
                break;
            case "MEDICO":
                setupMedicoTabs();
                break;
            case "FARMACEUTA":
                setupFarmaceutaTabs();
                break;
            case "PACIENTE":
                setupPacienteTabs();
                break;
            default:
                JOptionPane.showMessageDialog(this, "Rol desconocido: " + rol);
                break;
        }
    }

    private void setupAdminTabs() {
        MainTabPanel.addTab("📋 Pacientes", new PacienteView());
        MainTabPanel.addTab("👨‍⚕️ Médicos", new MedicoView());
        MainTabPanel.addTab("💊 Farmacéutas", new FarmaceutaView());
        MainTabPanel.addTab("💉 Medicamentos", new MedicamentosView());
    }

    private void setupMedicoTabs() {
        MainTabPanel.addTab("📝 Prescribir", new PrescribirView());
        MainTabPanel.addTab("📋 Pacientes", new PacienteView());
    }

    private void setupFarmaceutaTabs() {
        MainTabPanel.addTab("📦 Despacho", new DespachoView());
        MainTabPanel.addTab("💉 Medicamentos", new MedicamentosView());
    }

    private void setupPacienteTabs() {
        MainTabPanel.addTab("📜 Mis Recetas", new HistoricoRecetaView());
    }

    private void setupListeners() {
        LogoutButton.addActionListener(e -> performLogout());

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(
                        MainWindow.this,
                        "¿Está seguro que desea salir?",
                        "Confirmar Salida",
                        JOptionPane.YES_NO_OPTION
                );

                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    private void performLogout() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea cerrar sesión?",
                "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(() -> {
                LoginView loginView = new LoginView();
                loginView.setVisible(true);
                dispose();
            });
        }
    }
}