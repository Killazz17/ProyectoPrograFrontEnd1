package Presentation.Views;

import Domain.Dtos.ResponseDto;
import Presentation.Controllers.LoginController;
import Services.ApiClient;
import Services.AuthService;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginView extends JFrame {
    private JPanel ContentPanel;
    private JPanel LoginPanel;
    private JPanel FormPanel;
    private JPanel IDGroupPanel;
    private JLabel IDLabel;
    private JTextField IDTextField;
    private JPanel PasswordGroupPanel;
    private JLabel PasswordLabel;
    private JPasswordField PasswordTextFiel;
    private JPanel ImagePanel;
    private JLabel ImageLabel;
    private JPanel ButtonPanel;
    private JButton ClearButton;
    private JButton ChangePasswordButton;
    private JButton LoginButton;

    private final LoginController controller;
    private final ApiClient apiClient;

    public LoginView() {
        this.controller = new LoginController();
        this.apiClient = ApiClient.getInstance();

        setupFrame();
        setupListeners();

        // Intentar conectar al servidor
        if (!apiClient.connect()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo conectar al servidor.\nAsegúrese de que el backend esté corriendo en puerto 7070.",
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupFrame() {
        setContentPane(ContentPanel);
        setTitle("Sistema Hospital - Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void setupListeners() {
        LoginButton.addActionListener(e -> performLogin());
        ClearButton.addActionListener(e -> clearFields());
        ChangePasswordButton.setEnabled(false);

        PasswordTextFiel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });

        IDTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    PasswordTextFiel.requestFocus();
                }
            }
        });
    }

    private void performLogin() {
        String idText = IDTextField.getText().trim();
        String password = new String(PasswordTextFiel.getPassword());

        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese su ID",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            IDTextField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese su contraseña",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            PasswordTextFiel.requestFocus();
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un número válido",
                    "ID inválido", JOptionPane.ERROR_MESSAGE);
            IDTextField.requestFocus();
            return;
        }

        setButtonsEnabled(false);

        SwingWorker<ResponseDto, Void> worker = new SwingWorker<>() {
            @Override
            protected ResponseDto doInBackground() {
                return controller.login(id, password);
            }

            @Override
            protected void done() {
                try {
                    ResponseDto response = get();
                    handleLoginResponse(response);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LoginView.this,
                            "Error al procesar login: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    setButtonsEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void handleLoginResponse(ResponseDto response) {
        if (response.isSuccess()) {
            AuthService.UserSession user = controller.getCurrentUser();
            JOptionPane.showMessageDialog(this,
                    "Bienvenido " + user.getNombre() + "\nRol: " + user.getRol(),
                    "Login Exitoso", JOptionPane.INFORMATION_MESSAGE);

            openMainWindow();

        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(),
                    "Error de Login", JOptionPane.ERROR_MESSAGE);
            setButtonsEnabled(true);
            clearFields();
            IDTextField.requestFocus();
        }
    }

    private void openMainWindow() {
        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow(controller.getAuthService());
            mainWindow.setVisible(true);
            dispose();
        });
    }

    private void clearFields() {
        IDTextField.setText("");
        PasswordTextFiel.setText("");
        IDTextField.requestFocus();
    }

    private void setButtonsEnabled(boolean enabled) {
        LoginButton.setEnabled(enabled);
        ClearButton.setEnabled(enabled);
        IDTextField.setEnabled(enabled);
        PasswordTextFiel.setEnabled(enabled);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginView login = new LoginView();
            login.setVisible(true);
        });
    }
}