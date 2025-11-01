package Presentation.Views;

import Domain.Dtos.LoginResponseDto;
import Presentation.IObserver;
import Utilities.EventType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginView extends JFrame implements IObserver {
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
    private Presentation.Controllers.LoginController controller;

    public LoginView() {
        createUIComponents(); // Crear componentes manualmente
        setupFrame();
        setupListeners();
    }
    
    /**
     * Establece el controlador para esta vista
     */
    public void setController(Presentation.Controllers.LoginController controller) {
        this.controller = controller;
    }

    /**
     * Crear todos los componentes de la UI manualmente (sin GUI Designer)
     */
    private void createUIComponents() {
        // Panel principal
        ContentPanel = new JPanel(new GridBagLayout());
        ContentPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Imagen de usuario (usando texto como placeholder)
        ImagePanel = new JPanel();
        ImageLabel = new JLabel("👤", SwingConstants.CENTER);
        ImageLabel.setFont(new Font("Dialog", Font.PLAIN, 72));
        ImagePanel.add(ImageLabel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        ContentPanel.add(ImagePanel, gbc);

        // Panel de formulario
        FormPanel = new JPanel(new GridLayout(2, 1, 5, 10));

        // ID
        IDGroupPanel = new JPanel(new BorderLayout(10, 0));
        IDLabel = new JLabel("ID:");
        IDTextField = new JTextField(15);
        IDGroupPanel.add(IDLabel, BorderLayout.WEST);
        IDGroupPanel.add(IDTextField, BorderLayout.CENTER);

        // Password
        PasswordGroupPanel = new JPanel(new BorderLayout(10, 0));
        PasswordLabel = new JLabel("Password:");
        PasswordTextFiel = new JPasswordField(15);
        PasswordGroupPanel.add(PasswordLabel, BorderLayout.WEST);
        PasswordGroupPanel.add(PasswordTextFiel, BorderLayout.CENTER);

        FormPanel.add(IDGroupPanel);
        FormPanel.add(PasswordGroupPanel);

        gbc.gridy = 1;
        gbc.gridwidth = 2;
        ContentPanel.add(FormPanel, gbc);

        // Panel de botones
        ButtonPanel = new JPanel(new GridLayout(1, 3, 5, 0));

        LoginButton = new JButton("Entrar");
        LoginButton.setBackground(new Color(76, 175, 80));
        LoginButton.setForeground(Color.WHITE);
        LoginButton.setFocusPainted(false);

        ClearButton = new JButton("Limpiar");
        ClearButton.setBackground(new Color(158, 158, 158));
        ClearButton.setForeground(Color.WHITE);
        ClearButton.setFocusPainted(false);

        ChangePasswordButton = new JButton("Cambiar Clave");
        ChangePasswordButton.setBackground(new Color(33, 150, 243));
        ChangePasswordButton.setForeground(Color.WHITE);
        ChangePasswordButton.setEnabled(false);
        ChangePasswordButton.setFocusPainted(false);

        ButtonPanel.add(LoginButton);
        ButtonPanel.add(ClearButton);
        ButtonPanel.add(ChangePasswordButton);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        ContentPanel.add(ButtonPanel, gbc);
    }

    private void setupFrame() {
        setContentPane(ContentPanel);
        setTitle("Sistema Hospital - Login");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void setupListeners() {
        LoginButton.addActionListener(e -> performLogin());
        ClearButton.addActionListener(e -> clearFields());

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
        if (controller == null) {
            JOptionPane.showMessageDialog(this, "Error: No se ha configurado el controlador",
                    "Error de configuración", JOptionPane.ERROR_MESSAGE);
            return;
        }

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
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<LoginResponseDto, Void> worker = new SwingWorker<>() {
            @Override
            protected LoginResponseDto doInBackground() {
                return controller.login(id, password);
            }

            @Override
            protected void done() {
                try {
                    get(); // La respuesta se manejará en el método update()
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LoginView.this,
                            "Error al procesar login: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    setButtonsEnabled(true);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
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
            IDTextField.requestFocus();
        }
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