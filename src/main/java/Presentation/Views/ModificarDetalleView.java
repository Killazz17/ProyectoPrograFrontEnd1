package Presentation.Views;

import javax.swing.*;
import java.awt.*;

public class ModificarDetalleView extends JDialog {
    private JPanel ModifyDetailMainPanel;
    private JPanel MainPanel;
    private JPanel ModifyPanel;
    private JLabel QuantityLabel;
    private JLabel DaysLabel;
    private JLabel IndicatiionLabel;
    private JSpinner QuantitySpinner;
    private JSpinner DaysSpinner;
    private JTextField IndicationsTexField;
    private JButton SaveButton;
    private JButton CancelButton;

    private boolean confirmed = false;
    private int cantidad;
    private int duracion;
    private String indicaciones;

    public ModificarDetalleView(Frame owner) {
        this(owner, 1, 7, "");
    }

    public ModificarDetalleView(Frame owner, int cantidadInicial, int duracionInicial, String indicacionesInicial) {
        super(owner, "Detalles del Medicamento", true);

        this.cantidad = cantidadInicial;
        this.duracion = duracionInicial;
        this.indicaciones = indicacionesInicial;

        initComponents();
        setupFrame();
        setupEvents();
    }

    private void initComponents() {
        ModifyDetailMainPanel = new JPanel(new BorderLayout(10, 10));
        ModifyDetailMainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        MainPanel = new JPanel(new BorderLayout());
        MainPanel.setBorder(BorderFactory.createTitledBorder("Medicamento"));

        ModifyPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Cantidad
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        QuantityLabel = new JLabel("Cantidad:");
        ModifyPanel.add(QuantityLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        QuantitySpinner = new JSpinner(new SpinnerNumberModel(cantidad, 1, 100, 1));
        ModifyPanel.add(QuantitySpinner, gbc);

        // Duración
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        DaysLabel = new JLabel("Duración (Días):");
        ModifyPanel.add(DaysLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        DaysSpinner = new JSpinner(new SpinnerNumberModel(duracion, 1, 365, 1));
        ModifyPanel.add(DaysSpinner, gbc);

        // Indicaciones
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        IndicatiionLabel = new JLabel("Indicaciones:");
        ModifyPanel.add(IndicatiionLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        IndicationsTexField = new JTextField(indicaciones, 30);
        ModifyPanel.add(IndicationsTexField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        SaveButton = new JButton("Guardar");
        SaveButton.setIcon(new ImageIcon(getClass().getResource("/icons/diskette.png")));
        buttonPanel.add(SaveButton);

        CancelButton = new JButton("Cancelar");
        CancelButton.setIcon(new ImageIcon(getClass().getResource("/icons/multiply.png")));
        buttonPanel.add(CancelButton);

        ModifyPanel.add(buttonPanel, gbc);

        MainPanel.add(ModifyPanel, BorderLayout.CENTER);
        ModifyDetailMainPanel.add(MainPanel, BorderLayout.CENTER);
    }

    private void setupFrame() {
        setContentPane(ModifyDetailMainPanel);
        pack();
        setMinimumSize(new Dimension(400, 250));
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void setupEvents() {
        SaveButton.addActionListener(e -> {
            cantidad = (Integer) QuantitySpinner.getValue();
            duracion = (Integer) DaysSpinner.getValue();
            indicaciones = IndicationsTexField.getText().trim();

            if (indicaciones.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Por favor, ingrese las indicaciones para este medicamento.",
                        "Campo requerido",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            confirmed = true;
            dispose();
        });

        CancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getDuracion() {
        return duracion;
    }

    public String getIndicaciones() {
        return indicaciones;
    }
}