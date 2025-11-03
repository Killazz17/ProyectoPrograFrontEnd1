package Presentation.Views;

import Domain.Dtos.LoginResponseDto;
import javax.swing.*;
import java.awt.*;

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

    public MainWindow(LoginResponseDto usuario) {
        this.usuario = usuario;

        createUIComponents();
        setupFrame();
        setupTabs();
        setupListeners();
    }

    private void createUIComponents() {
        ContentPanel = new JPanel(new BorderLayout());

        // Toolbar superior
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        // Información del usuario
        JLabel userInfoLabel = new JLabel("Usuario: " + usuario.getNombre() + " | Rol: " + usuario.getRol());
        userInfoLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        toolbar.add(userInfoLabel);

        // Spacer
        toolbar.add(Box.createHorizontalGlue());

        // Botón de logout
        LogoutButton = new JButton("Cerrar Sesión");
        LogoutButton.setFocusPainted(false);
        toolbar.add(LogoutButton);

        ContentPanel.add(toolbar, BorderLayout.NORTH);

        // Panel de pestañas
        MainTabPanel = new JTabbedPane();
        ContentPanel.add(MainTabPanel, BorderLayout.CENTER);
    }

    private void setupFrame() {
        setContentPane(ContentPanel);
        setTitle("Sistema Hospital - " + usuario.getNombre() + " (" + usuario.getRol() + ")");
        setSize(1200, 768);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void setupTabs() {
        String rol = usuario.getRol();

        if (rol == null) {
            JOptionPane.showMessageDialog(this, "Rol no definido");
            return;
        }

        System.out.println("[MainWindow] Configurando pestañas para rol: " + rol);

        switch (rol.toUpperCase()) {
            case "ADMIN":
            case "ADMINISTRADOR":
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

        // Si no se agregaron pestañas, mostrar error
        if (MainTabPanel.getTabCount() == 0) {
            JPanel errorPanel = new JPanel(new BorderLayout());
            JLabel errorLabel = new JLabel(
                    "No se configuraron pestañas para el rol: " + rol,
                    SwingConstants.CENTER
            );
            errorLabel.setFont(new Font("Dialog", Font.BOLD, 16));
            errorPanel.add(errorLabel, BorderLayout.CENTER);
            MainTabPanel.addTab("Error", errorPanel);
        }
    }

    private void setupAdminTabs() {
        System.out.println("[MainWindow] Agregando pestañas de administrador");

        // ✅ Agregar los JPanel directamente (no usar getContentPane)
        PacienteView pacienteView = new PacienteView();
        MainTabPanel.addTab("Pacientes", pacienteView);

        MedicoView medicoView = new MedicoView();
        MainTabPanel.addTab("Médicos", medicoView);

        FarmaceutaView farmaceutaView = new FarmaceutaView();
        MainTabPanel.addTab("Farmacéutas", farmaceutaView);

        MedicamentosView medicamentosView = new MedicamentosView();
        MainTabPanel.addTab("Medicamentos", medicamentosView);

        //DashboardView dashboardView= new DashboardView();
        //MainTabPanel.addTab("Dashboard", dashboardView);

        HistoricoRecetaView historicoRecetaView = new HistoricoRecetaView();
        MainTabPanel.addTab("Historico", historicoRecetaView);


        System.out.println("[MainWindow] Pestañas de admin agregadas: " + MainTabPanel.getTabCount());
    }

    private void setupMedicoTabs() {
        System.out.println("[MainWindow] Agregando pestañas de médico");

        PrescribirView prescribirView = new PrescribirView();
        MainTabPanel.addTab("Prescribir", prescribirView);

        HistoricoRecetaView historicoRecetaView = new HistoricoRecetaView();
        MainTabPanel.addTab("Historico", historicoRecetaView);

        DespachoView despachoView = new DespachoView();
        MainTabPanel.addTab("Despacho", despachoView);

        System.out.println("[MainWindow] Pestañas de médico agregadas: " + MainTabPanel.getTabCount());
    }

    private void setupFarmaceutaTabs() {
        System.out.println("[MainWindow] Agregando pestañas de farmaceuta");

        DespachoView despachoView = new DespachoView();
        MainTabPanel.addTab("Despacho", despachoView);

        MedicamentosView medicamentosView = new MedicamentosView();
        MainTabPanel.addTab("Medicamentos", medicamentosView);

        HistoricoRecetaView historicoRecetaView = new HistoricoRecetaView();
        MainTabPanel.addTab("Historico", historicoRecetaView);

        System.out.println("[MainWindow] Pestañas de farmaceuta agregadas: " + MainTabPanel.getTabCount());
    }

    private void setupPacienteTabs() {
        System.out.println("[MainWindow] Agregando pestañas de paciente");

        DespachoView despachoView = new DespachoView();
        MainTabPanel.addTab("Despacho", despachoView);

        System.out.println("[MainWindow] Pestañas de paciente agregadas: " + MainTabPanel.getTabCount());
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
                Presentation.Controllers.LoginController loginController =
                        new Presentation.Controllers.LoginController(loginView, new Services.AuthService());
                loginController.addObserver(loginView);
                loginView.setController(loginController);
                loginView.setVisible(true);
                dispose();
            });
        }
    }
}