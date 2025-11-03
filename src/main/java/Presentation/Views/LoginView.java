package Presentation.Views;

import Domain.Dtos.LoginResponseDto;
import Presentation.IObserver;
import Services.AuthService;
import Utilities.EventType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginView extends JFrame implements IObserver {
    // Componentes generados por el .form
    private JPanel ContentPane;
    private JPanel ImagePane;
    private JPanel LoginForm;
    private JPanel FieldsPane;
    private JPanel UserFormPanel;
    private JLabel UserLabel;
    private JTextField UserField;
    private JPanel PasswordFormPanel;
    private JLabel PasswordLabel;
    private JPasswordField PasswordField;
    private JPanel ButtonPanel;
    private JButton LogginButton;
    private JButton CleanFieldsButton;
    private JButton ChangePasswordButton;

    private LoginResponseDto responseData;
    private Presentation.Controllers.LoginController controller;
    private AuthService authService;

    public LoginView() {
        setupFrame();
        setupListeners();
        UserField.setForeground(Color.BLACK);
        PasswordField.setForeground(Color.BLACK);
    }

    /**
     * Establece el controlador para esta vista
     */
    public void setController(Presentation.Controllers.LoginController controller) {
        this.controller = controller;
    }

    /**
     * Establece el servicio de autenticación
     */
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    private void setupFrame() {
        setContentPane(ContentPane);
        setTitle("Sistema Hospital - Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void setupListeners() {
        // Botón Ingresar
        LogginButton.addActionListener(e -> performLogin());

        // Botón Limpiar
        CleanFieldsButton.addActionListener(e -> clearFields());

        // Botón Cambiar Contraseña - AHORA HABILITADO
        ChangePasswordButton.setEnabled(true);
        ChangePasswordButton.addActionListener(e -> openChangePassword());

        // Enter en el campo de password ejecuta login
        PasswordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });

        // Enter en el campo de usuario mueve el foco a password
        UserField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    PasswordField.requestFocus();
                }
            }
        });
    }

    private void openChangePassword() {
        if (authService == null) {
            JOptionPane.showMessageDialog(this,
                    "Error: Servicio de autenticación no configurado",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        ChangePasswordView changePasswordView = new ChangePasswordView(this, authService);
        changePasswordView.setVisible(true);
    }

    private void performLogin() {
        if (controller == null) {
            JOptionPane.showMessageDialog(this, "Error: No se ha configurado el controlador",
                    "Error de configuración", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String usuario = UserField.getText().trim();
        String clave = new String(PasswordField.getPassword());

        if (usuario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese su usuario",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            UserField.requestFocus();
            return;
        }

        if (clave.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese su clave",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            PasswordField.requestFocus();
            return;
        }

        setButtonsEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                // ✅ Usar loginByNombre para login con nombre de usuario
                controller.loginByNombre(usuario, clave);
                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
            }
        };

        worker.execute();
    }

    private void handleLoginResponse(LoginResponseDto response) {
        if (response.isSuccess()) {
            this.responseData = response;

            // Mostrar mensaje de bienvenida
            String mensaje = String.format(
                    "¡Bienvenido!\n\nUsuario: %s\nRol: %s",
                    response.getNombre(),
                    response.getRol()
            );

            JOptionPane.showMessageDialog(
                    this,
                    mensaje,
                    "Login exitoso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Abrir ventana principal
            SwingUtilities.invokeLater(() -> {
                MainWindow mainWindow = new MainWindow(response);
                mainWindow.setVisible(true);
                dispose();
            });

        } else {
            JOptionPane.showMessageDialog(
                    this,
                    response.getMensaje(),
                    "Error de login",
                    JOptionPane.ERROR_MESSAGE
            );
            setButtonsEnabled(true);
            clearFields();
            UserField.requestFocus();
        }
    }

    private void clearFields() {
        UserField.setText("");
        PasswordField.setText("");
        UserField.requestFocus();
    }

    private void setButtonsEnabled(boolean enabled) {
        LogginButton.setEnabled(enabled);
        CleanFieldsButton.setEnabled(enabled);
        ChangePasswordButton.setEnabled(enabled);
        UserField.setEnabled(enabled);
        PasswordField.setEnabled(enabled);
    }

    /**
     * Implementación del método update de IObserver
     * Se invoca cuando el controlador notifica cambios
     */
    @Override
    public void update(EventType eventType, Object data) {
        if (data instanceof LoginResponseDto) {
            LoginResponseDto response = (LoginResponseDto) data;

            // Ejecutar en el Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                if (eventType == EventType.CREATED) {
                    // Login exitoso
                    handleLoginResponse(response);
                } else if (eventType == EventType.DELETED) {
                    // Login fallido
                    handleLoginResponse(response);
                }
            });
        }
    }
}