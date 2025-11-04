package Presentation.Views;

import Domain.Dtos.LoginResponseDto;
import hospital.Cliente.WebSocketClienteMensajeria;
import hospital.ui.PanelUsuarios;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class MainWindow extends JFrame {
    private JPanel ContentPanel;
    private JTabbedPane MainTabPanel;
    private JButton LogoutButton;
    private final LoginResponseDto usuario;

    // NUEVOS: Para mensajería
    private WebSocketClienteMensajeria clienteWS;
    private PanelUsuarios panelUsuarios;

    public MainWindow(LoginResponseDto usuario) {
        this.usuario = usuario;

        createUIComponents();
        setupFrame();
        setupTabs();
        setupListeners();

        // NUEVO: Inicializar sistema de mensajería
        inicializarSistemaMensajeria();
    }

    private void createUIComponents() {
        ContentPanel = new JPanel(new BorderLayout());

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        JLabel userInfoLabel = new JLabel("Usuario: " + usuario.getNombre() + " | Rol: " + usuario.getRol());
        userInfoLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        toolbar.add(userInfoLabel);

        toolbar.add(Box.createHorizontalGlue());

        LogoutButton = new JButton("Cerrar Sesión");
        LogoutButton.setFocusPainted(false);
        toolbar.add(LogoutButton);

        ContentPanel.add(toolbar, BorderLayout.NORTH);

        MainTabPanel = new JTabbedPane();
        ContentPanel.add(MainTabPanel, BorderLayout.CENTER);
    }

    private void setupFrame() {
        setContentPane(ContentPanel);
        setTitle("Sistema Hospital - " + usuario.getNombre() + " (" + usuario.getRol() + ")");
        setSize(1400, 768); // AUMENTADO el ancho para incluir panel de mensajes
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // NUEVO MÉTODO: Inicializar sistema de mensajería
    private void inicializarSistemaMensajeria() {
        try {
            // Crear ID único basado en el usuario real
            String userId = usuario.getId() + "-" + usuario.getRol().toUpperCase();
            String userName = usuario.getNombre() + " (" + usuario.getRol() + ")";

            // Conectar al servidor WebSocket
            URI serverUri = new URI("ws://localhost:8887");
            clienteWS = new WebSocketClienteMensajeria(serverUri);

            // Crear panel de usuarios
            panelUsuarios = new PanelUsuarios(clienteWS, userId);

            // Configurar callbacks
            clienteWS.configurarCallbacks(
                    usuarios -> panelUsuarios.actualizarUsuarios(usuarios),
                    mensaje -> panelUsuarios.mostrarMensajeRecibido(mensaje)
            );

            // Agregar panel al lado derecho de la ventana principal
            ContentPanel.add(panelUsuarios, BorderLayout.EAST);

            // Conectar y registrar usuario
            clienteWS.connect();

            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    clienteWS.registrarUsuario(userId, userName);
                    System.out.println("✅ Sistema de mensajería iniciado para: " + userName);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            System.err.println("⚠️ No se pudo conectar al sistema de mensajería: " + e.getMessage());
            // La aplicación continúa funcionando sin mensajería
        }
    }

    private void setupTabs() {
        String rol = usuario.getRol();

        if (rol == null) {
            JOptionPane.showMessageDialog(this, "Rol no definido");
            return;
        }

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
        PacienteView pacienteView = new PacienteView();
        MainTabPanel.addTab("Pacientes", pacienteView);

        MedicoView medicoView = new MedicoView();
        MainTabPanel.addTab("Médicos", medicoView);

        FarmaceutaView farmaceutaView = new FarmaceutaView();
        MainTabPanel.addTab("Farmacéutas", farmaceutaView);

        MedicamentosView medicamentosView = new MedicamentosView();
        MainTabPanel.addTab("Medicamentos", medicamentosView);

        DashboardView dashboardView= new DashboardView();
        MainTabPanel.addTab("Dashboard", dashboardView);

        HistoricoRecetaView historicoRecetaView = new HistoricoRecetaView();
        MainTabPanel.addTab("Historico", historicoRecetaView);
    }

    private void setupMedicoTabs() {
        PrescribirView prescribirView = new PrescribirView();
        MainTabPanel.addTab("Prescribir", prescribirView);

        HistoricoRecetaView historicoRecetaView = new HistoricoRecetaView();
        MainTabPanel.addTab("Historico", historicoRecetaView);

        DespachoView despachoView = new DespachoView(usuario);
        MainTabPanel.addTab("Despacho", despachoView);

        DashboardView dashboardView = new DashboardView();
        MainTabPanel.addTab("Dashboard", dashboardView);
    }

    private void setupFarmaceutaTabs() {
        DespachoView despachoView = new DespachoView(usuario);
        MainTabPanel.addTab("Despacho", despachoView);

        MedicamentosView medicamentosView = new MedicamentosView();
        MainTabPanel.addTab("Medicamentos", medicamentosView);

        HistoricoRecetaView historicoRecetaView = new HistoricoRecetaView();
        MainTabPanel.addTab("Historico", historicoRecetaView);

        DashboardView dashboardView = new DashboardView();
        MainTabPanel.addTab("Dashboard", dashboardView);
    }

    private void setupPacienteTabs() {
        DespachoView despachoView = new DespachoView(usuario);
        MainTabPanel.addTab("Despacho", despachoView);
    }

    private void setupListeners() {
        LogoutButton.addActionListener(e -> performLogout());

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(
                        MainWindow.this,
                        "¿Esta seguro que desea salir?",
                        "Confirmar Salida",
                        JOptionPane.YES_NO_OPTION
                );

                if (result == JOptionPane.YES_OPTION) {
                    cerrarConexiones();
                    System.exit(0);
                }
            }
        });
    }

    private void performLogout() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "¿Esta seguro que desea cerrar sesion?",
                "Cerrar Sesion",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            cerrarConexiones();

            SwingUtilities.invokeLater(() -> {
                LoginView loginView = new LoginView();
                Presentation.Controllers.LoginController loginController =
                        new Presentation.Controllers.LoginController(loginView, new Services.AuthService());
                loginController.addObserver(loginView);
                loginView.setController(loginController);
                loginView.setAuthService(new Services.AuthService());
                loginView.setVisible(true);
                dispose();
            });
        }
    }

    // NUEVO MÉTODO: Cerrar conexiones WebSocket al salir
    private void cerrarConexiones() {
        if (clienteWS != null && clienteWS.isOpen()) {
            clienteWS.close();
            System.out.println("✅ Desconectado del sistema de mensajería");
        }
    }

    @Override
    public void dispose() {
        cerrarConexiones();
        super.dispose();
    }
}