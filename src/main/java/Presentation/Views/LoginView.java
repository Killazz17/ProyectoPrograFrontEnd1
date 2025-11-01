package Presentation.Views;

import Domain.Dtos.LoginResponseDto;

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

    private LoginResponseDto responseData;
    private final Presentation.Controllers.AuthController controller;

    public LoginView() {
        this.controller = new Presentation.Controllers.AuthController();

        setupFrame();
        setupListeners();
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

        SwingWorker<LoginResponseDto, Void> worker = new SwingWorker<>() {
            @Override
            protected LoginResponseDto doInBackground() {
                return controller.login(id, password);
            }

            @Override
            protected void done() {
                try {
                    LoginResponseDto response = get();
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

    /**
     * Maneja la respuesta del login después de recibirla del AuthController.
     * Si es exitoso, abre la ventana principal; si no, muestra el mensaje de error.
     */
    private void handleLoginResponse(LoginResponseDto response) {
        if (response.isSuccess()) {
            this.responseData = response;
            JOptionPane.showMessageDialog(
                    this,
                    "Bienvenido " + response.getNombre() + " (" + response.getRol() + ")",
                    "Login exitoso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Abre la ventana principal con el usuario autenticado
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
            IDTextField.requestFocus();
        }
    }

    private void openMainWindow() {
        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow(responseData);
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
}