package Presentation.Views;

import Domain.Dtos.PacienteDto;
import Presentation.Controllers.PacienteController;
import Presentation.IObserver;
import Services.PacienteService;
import Utilities.EventType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PacienteView extends JPanel implements IObserver {
    private JPanel ContentPanel;
    private JPanel FormPanel;
    private JPanel FormGroupPanel;
    private JPanel IdPanel;
    private JLabel IdLabel;
    private JTextField IdTextField;
    private JPanel NombrePanel;
    private JLabel NombreLabel;
    private JTextField NombreTextField;
    private JPanel FechaNacimientoPanel;
    private JLabel FechaNacimientoLabel;
    private JTextField FechaNacimientoTextField;
    private JPanel TelefonoPanel;
    private JLabel TelefonoLabel;
    private JTextField TelefonoTextField;
    private JPanel ButtonPanel;
    private JButton guardarButton;
    private JButton limpiarButton;
    private JButton borrarButton;
    private JPanel SearchPanel;
    private JPanel SearchNamePanel;
    private JTextField SearchNombreTextField;
    private JLabel SearchNombreLabel;
    private JButton searchButton;
    private JButton reporteButton;
    private JTable table;

    private final PacienteController controller;

    public PacienteView() {
        controller = new PacienteController(new PacienteService());
        controller.addObserver(this);

        setLayout(new java.awt.BorderLayout());

        if (ContentPanel == null) {
            createManualUI();
        } else {
            add(ContentPanel, java.awt.BorderLayout.CENTER);
            setupEvents();
        }

        controller.listarPacientesAsync();

        IdTextField.setForeground(Color.BLACK);
        NombreTextField.setForeground(Color.BLACK);
        FechaNacimientoTextField.setForeground(Color.BLACK);
        TelefonoTextField.setForeground(Color.BLACK);
        SearchNombreTextField.setForeground(Color.BLACK);
    }

    private void createManualUI() {
        setLayout(new java.awt.BorderLayout(10, 10));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        javax.swing.JPanel formPanel = new javax.swing.JPanel(new java.awt.GridBagLayout());
        formPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Paciente"));
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new javax.swing.JLabel("ID:"), gbc);
        gbc.gridx = 1;
        IdTextField = new javax.swing.JTextField(15);
        formPanel.add(IdTextField, gbc);

        gbc.gridx = 2;
        formPanel.add(new javax.swing.JLabel("Nombre:"), gbc);
        gbc.gridx = 3;
        NombreTextField = new javax.swing.JTextField(15);
        formPanel.add(NombreTextField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new javax.swing.JLabel("Fecha (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        FechaNacimientoTextField = new javax.swing.JTextField(15);
        formPanel.add(FechaNacimientoTextField, gbc);

        gbc.gridx = 2;
        formPanel.add(new javax.swing.JLabel("Telefono:"), gbc);
        gbc.gridx = 3;
        TelefonoTextField = new javax.swing.JTextField(15);
        formPanel.add(TelefonoTextField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        javax.swing.JPanel btnPanel = new javax.swing.JPanel(new java.awt.FlowLayout());
        guardarButton = new javax.swing.JButton("Guardar");
        limpiarButton = new javax.swing.JButton("Limpiar");
        borrarButton = new javax.swing.JButton("Borrar");
        btnPanel.add(guardarButton);
        btnPanel.add(limpiarButton);
        btnPanel.add(borrarButton);
        formPanel.add(btnPanel, gbc);

        add(formPanel, java.awt.BorderLayout.NORTH);

        javax.swing.JPanel searchPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        searchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Busqueda"));
        searchPanel.add(new javax.swing.JLabel("Nombre:"));
        SearchNombreTextField = new javax.swing.JTextField(20);
        searchPanel.add(SearchNombreTextField);
        searchButton = new javax.swing.JButton("Buscar");
        searchPanel.add(searchButton);
        reporteButton = new javax.swing.JButton("Reporte");
        searchPanel.add(reporteButton);
        add(searchPanel, java.awt.BorderLayout.CENTER);

        table = new javax.swing.JTable();
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(table);
        scrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Listado"));
        add(scrollPane, java.awt.BorderLayout.SOUTH);

        setupEvents();
    }

    private void setupEvents() {
        guardarButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(IdTextField.getText());
                String nombre = NombreTextField.getText().trim();
                String fecha = FechaNacimientoTextField.getText().trim();
                String telefono = TelefonoTextField.getText().trim();

                if (nombre.isEmpty() || fecha.isEmpty() || telefono.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
                    return;
                }

                PacienteDto dto = new PacienteDto(id, nombre, fecha, telefono);
                controller.crearPacienteAsync(dto);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID invalido.");
            }
        });

        borrarButton.addActionListener(e -> {
            int fila = table.getSelectedRow();
            if (fila != -1) {
                int id = (int) table.getValueAt(fila, 0);
                controller.eliminarPacienteAsync(id);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un paciente.");
            }
        });

        limpiarButton.addActionListener(e -> limpiarCampos());

        searchButton.addActionListener(e -> {
            String nombre = SearchNombreTextField.getText().trim();
            if (!nombre.isEmpty()) controller.buscarPacienteAsync(nombre);
            else controller.listarPacientesAsync();
        });

        reporteButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Funcionalidad de reporte aun no implementada.")
        );
    }

    private void limpiarCampos() {
        IdTextField.setText("");
        NombreTextField.setText("");
        FechaNacimientoTextField.setText("");
        TelefonoTextField.setText("");
        SearchNombreTextField.setText("");
    }

    @Override
    public void update(EventType eventType, Object data) {
        switch (eventType) {
            case UPDATED -> actualizarTabla((List<PacienteDto>) data);
            case CREATED -> JOptionPane.showMessageDialog(this, "Paciente creado correctamente.");
            case DELETED -> JOptionPane.showMessageDialog(this, "Paciente eliminado correctamente.");
        }
    }

    private void actualizarTabla(List<PacienteDto> pacientes) {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Nombre", "Fecha Nacimiento", "Telefono"}, 0);
        for (PacienteDto p : pacientes) {
            model.addRow(new Object[]{p.getId(), p.getNombre(), p.getFechaNacimiento(), p.getTelefono()});
        }
        table.setModel(model);
    }
}