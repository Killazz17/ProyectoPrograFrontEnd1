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

    public void setController(Presentation.Controllers.LoginController controller) {
        this.controller = controller;
    }

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
        LogginButton.addActionListener(e -> performLogin());

        CleanFieldsButton.addActionListener(e -> clearFields());

        ChangePasswordButton.setEnabled(true);
        ChangePasswordButton.addActionListener(e -> openChangePassword());

        PasswordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });

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
                    "Error: Servicio de autenticacion no configurado",
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
                    "Error de configuracion", JOptionPane.ERROR_MESSAGE);
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

    @Override
    public void update(EventType eventType, Object data) {
        if (data instanceof LoginResponseDto) {
            LoginResponseDto response = (LoginResponseDto) data;

            SwingUtilities.invokeLater(() -> {
                if (eventType == EventType.CREATED) {
                    handleLoginResponse(response);
                } else if (eventType == EventType.DELETED) {
                    handleLoginResponse(response);
                }
            });
        }
    }
}