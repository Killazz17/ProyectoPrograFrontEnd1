package Presentation.Views;

import Domain.Dtos.MedicoDto;
import Presentation.Controllers.MedicoController;
import Presentation.IObserver;
import Services.MedicoService;
import Utilities.EventType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MedicoView extends JPanel implements IObserver {
    private JPanel ContentPanel;
    private JPanel FormPanel;
    private JPanel FormGroupPanel;
    private JPanel IdPanel;
    private JLabel IdLabel;
    private JTextField IdTextField;
    private JPanel NombrePanel;
    private JLabel NombreLabel;
    private JTextField NombreTextField;
    private JPanel EspecialidadPanel;
    private JLabel EspecialidadLabel;
    private JTextField EspecialidadTextField;
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

    private final MedicoController controller;

    public MedicoView() {
        controller = new MedicoController(new MedicoService());
        controller.addObserver(this);

        setLayout(new java.awt.BorderLayout());

        if (ContentPanel == null) {
            createManualUI();
        } else {
            add(ContentPanel, java.awt.BorderLayout.CENTER);
            setupEvents();
        }

        controller.listarMedicosAsync();

        IdTextField.setForeground(Color.BLACK);
        NombreTextField.setForeground(Color.BLACK);
        EspecialidadTextField.setForeground(Color.BLACK);
        SearchNombreTextField.setForeground(Color.BLACK);
    }

    private void createManualUI() {
        setLayout(new java.awt.BorderLayout(10, 10));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Formulario
        javax.swing.JPanel formPanel = new javax.swing.JPanel(new java.awt.GridLayout(4, 2, 5, 5));
        formPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Médico"));

        formPanel.add(new javax.swing.JLabel("ID:"));
        IdTextField = new javax.swing.JTextField();
        formPanel.add(IdTextField);

        formPanel.add(new javax.swing.JLabel("Nombre:"));
        NombreTextField = new javax.swing.JTextField();
        formPanel.add(NombreTextField);

        formPanel.add(new javax.swing.JLabel("Especialidad:"));
        EspecialidadTextField = new javax.swing.JTextField();
        formPanel.add(EspecialidadTextField);

        guardarButton = new javax.swing.JButton("Guardar");
        borrarButton = new javax.swing.JButton("Borrar");
        formPanel.add(guardarButton);
        formPanel.add(borrarButton);

        add(formPanel, java.awt.BorderLayout.NORTH);

        // Búsqueda
        javax.swing.JPanel searchPanel = new javax.swing.JPanel();
        searchPanel.add(new javax.swing.JLabel("Buscar:"));
        SearchNombreTextField = new javax.swing.JTextField(20);
        searchPanel.add(SearchNombreTextField);
        searchButton = new javax.swing.JButton("Buscar");
        searchPanel.add(searchButton);
        limpiarButton = new javax.swing.JButton("Limpiar");
        searchPanel.add(limpiarButton);
        reporteButton = new javax.swing.JButton("Reporte");
        searchPanel.add(reporteButton);
        add(searchPanel, java.awt.BorderLayout.CENTER);

        // Tabla
        table = new javax.swing.JTable();
        add(new javax.swing.JScrollPane(table), java.awt.BorderLayout.SOUTH);

        setupEvents();
    }

    private void setupEvents() {
        guardarButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(IdTextField.getText());
                String nombre = NombreTextField.getText().trim();
                String especialidad = EspecialidadTextField.getText().trim();

                if (nombre.isEmpty() || especialidad.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
                    return;
                }

                MedicoDto dto = new MedicoDto(id, nombre, especialidad);
                controller.crearMedicoAsync(dto);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID inválido.");
            }
        });

        borrarButton.addActionListener(e -> {
            int fila = table.getSelectedRow();
            if (fila != -1) {
                int id = (int) table.getValueAt(fila, 0);
                controller.eliminarMedicoAsync(id);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un médico.");
            }
        });

        limpiarButton.addActionListener(e -> limpiarCampos());

        searchButton.addActionListener(e -> {
            String nombre = SearchNombreTextField.getText().trim();
            if (!nombre.isEmpty()) controller.buscarMedicoAsync(nombre);
            else controller.listarMedicosAsync();
        });

        reporteButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Funcionalidad de reporte aún no implementada.")
        );
    }

    private void limpiarCampos() {
        IdTextField.setText("");
        NombreTextField.setText("");
        EspecialidadTextField.setText("");
        SearchNombreTextField.setText("");
    }

    @Override
    public void update(EventType eventType, Object data) {
        switch (eventType) {
            case UPDATED -> actualizarTabla((List<MedicoDto>) data);
            case CREATED -> JOptionPane.showMessageDialog(this, "Médico creado correctamente.");
            case DELETED -> JOptionPane.showMessageDialog(this, "Médico eliminado correctamente.");
        }
    }

    private void actualizarTabla(List<MedicoDto> medicos) {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Nombre", "Especialidad"}, 0);
        for (MedicoDto m : medicos) {
            model.addRow(new Object[]{m.getId(), m.getNombre(), m.getEspecialidad()});
        }
        table.setModel(model);
    }
}