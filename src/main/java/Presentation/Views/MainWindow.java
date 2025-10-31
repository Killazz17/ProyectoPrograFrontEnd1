package Presentation.Views;

import Services.ApiClient;
import Services.AuthService;
import javax.swing.*;

public class MainWindow extends JFrame implements ApiClient.MessageListener {
    private JPanel ContentPanel;
    private JTabbedPane MainTabPanel;
    private JButton LogoutButton;

    private final AuthService authService;
    private final ApiClient apiClient;

    public MainWindow(AuthService authService) {
        this.authService = authService;
        this.apiClient = ApiClient.getInstance();

        setupFrame();
        setupTabs();
        setupListeners();

        apiClient.setMessageListener(this);
    }

    private void setupFrame() {
        setContentPane(ContentPanel);
        setTitle("Sistema Hospital - " + authService.getCurrentUser().getNombre());
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void setupTabs() {
        AuthService.UserSession user = authService.getCurrentUser();

        if (user.isAdmin()) {
            setupAdminTabs();
        } else if (user.isMedico()) {
            setupMedicoTabs();
        } else if (user.isFarmaceuta()) {
            setupFarmaceutaTabs();
        } else if (user.isPaciente()) {
            setupPacienteTabs();
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
                    apiClient.disconnect();
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
            authService.logout();

            SwingUtilities.invokeLater(() -> {
                LoginView loginView = new LoginView();
                loginView.setVisible(true);
                dispose();
            });
        }
    }

    @Override
    public void onMessageReceived(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    this,
                    message,
                    "Notificación del Servidor",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
    }
}