package Presentation.Views;

import Domain.Dtos.LoginResponseDto;
import Services.AuthService;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordView extends JDialog {
    private JPanel ContentPane;
    private JPanel FormPanel;
    private JPanel TittleLabelPanel;
    private JLabel ChangePasswordLabel;
    private JTextField UserField;
    private JLabel UserLabel;
    private JPanel UserFieldPanel;
    private JPasswordField ActualPasswordField;
    private JLabel ActualPasswordLabel;
    private JPanel ActualPasswordFieldPanel;
    private JPasswordField NewPasswordField;
    private JLabel NewPasswordLabel;
    private JPanel NewPasswordPanel;
    private JPanel ButtonPanel;
    private JButton CancelButton;
    private JButton AcceptButton;

    private final AuthService authService;

    public ChangePasswordView(JFrame parent, AuthService authService) {
        super(parent, "Cambiar Contraseña", true);
        this.authService = authService;

        initComponents();
        setupFrame();
        setupEvents();
    }

    private void initComponents() {
        if (ContentPane == null) {
            createManualUI();
        } else {
            setContentPane(ContentPane);
        }

        if (UserField != null) UserField.setForeground(Color.BLACK);
        if (ActualPasswordField != null) ActualPasswordField.setForeground(Color.BLACK);
        if (NewPasswordField != null) NewPasswordField.setForeground(Color.BLACK);
    }

    private void createManualUI() {
        ContentPane = new JPanel(new BorderLayout(10, 10));
        ContentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        TittleLabelPanel = new JPanel();
        ChangePasswordLabel = new JLabel("Cambio de Contraseña");
        ChangePasswordLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        TittleLabelPanel.add(ChangePasswordLabel);

        FormPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        UserLabel = new JLabel("Usuario:");
        FormPanel.add(UserLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        UserField = new JTextField(20);
        UserField.setForeground(Color.BLACK);
        FormPanel.add(UserField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        ActualPasswordLabel = new JLabel("Contraseña Actual:");
        FormPanel.add(ActualPasswordLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        ActualPasswordField = new JPasswordField(20);
        ActualPasswordField.setForeground(Color.BLACK);
        FormPanel.add(ActualPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        NewPasswordLabel = new JLabel("Nueva Contraseña:");
        FormPanel.add(NewPasswordLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        NewPasswordField = new JPasswordField(20);
        NewPasswordField.setForeground(Color.BLACK);
        FormPanel.add(NewPasswordField, gbc);

        ButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        AcceptButton = new JButton("Aceptar");
        CancelButton = new JButton("Cancelar");
        ButtonPanel.add(AcceptButton);
        ButtonPanel.add(CancelButton);

        ContentPane.add(TittleLabelPanel, BorderLayout.NORTH);
        ContentPane.add(FormPanel, BorderLayout.CENTER);
        ContentPane.add(ButtonPanel, BorderLayout.SOUTH);

        setContentPane(ContentPane);
    }

    private void setupFrame() {
        setSize(450, 300);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    private void setupEvents() {
        AcceptButton.addActionListener(e -> cambiarContrasena());
        CancelButton.addActionListener(e -> dispose());

        NewPasswordField.addActionListener(e -> cambiarContrasena());
    }

    private void cambiarContrasena() {
        String usuario = UserField.getText().trim();
        String claveActual = new String(ActualPasswordField.getPassword());
        String claveNueva = new String(NewPasswordField.getPassword());

        if (usuario.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese su nombre de usuario",
                    "Campo requerido",
                    JOptionPane.WARNING_MESSAGE);
            UserField.requestFocus();
            return;
        }

        if (claveActual.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese su contraseña actual",
                    "Campo requerido",
                    JOptionPane.WARNING_MESSAGE);
            ActualPasswordField.requestFocus();
            return;
        }

        if (claveNueva.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese su nueva contraseña",
                    "Campo requerido",
                    JOptionPane.WARNING_MESSAGE);
            NewPasswordField.requestFocus();
            return;
        }

        if (claveNueva.length() < 4) {
            JOptionPane.showMessageDialog(this,
                    "La nueva contraseña debe tener al menos 4 caracteres",
                    "Contraseña muy corta",
                    JOptionPane.WARNING_MESSAGE);
            NewPasswordField.requestFocus();
            return;
        }

        if (claveActual.equals(claveNueva)) {
            JOptionPane.showMessageDialog(this,
                    "La nueva contraseña debe ser diferente a la actual",
                    "Contraseñas iguales",
                    JOptionPane.WARNING_MESSAGE);
            NewPasswordField.requestFocus();
            return;
        }

        AcceptButton.setEnabled(false);
        CancelButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return authService.changePassword(usuario, claveActual, claveNueva);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                AcceptButton.setEnabled(true);
                CancelButton.setEnabled(true);

                try {
                    boolean success = get();

                    if (success) {
                        JOptionPane.showMessageDialog(ChangePasswordView.this,
                                "Contraseña cambiada exitosamente\n\n",
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(ChangePasswordView.this,
                                "Error al cambiar la contraseña\n\n" +
                                        "Verifique que su usuario y contraseña actual sean correctos.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        ActualPasswordField.setText("");
                        ActualPasswordField.requestFocus();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ChangePasswordView.this,
                            "Error: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void limpiarCampos() {
        UserField.setText("");
        ActualPasswordField.setText("");
        NewPasswordField.setText("");
        UserField.requestFocus();
    }
}