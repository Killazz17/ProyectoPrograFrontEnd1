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

        table = new javax.swing.JTable();
        add(new javax.swing.JScrollPane(table), java.awt.BorderLayout.SOUTH);

        setupEvents();
    }

    private void setupEvents() {
        guardarButton.addActionListener(e -> {
            // Validar ID
            if (IdTextField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "El ID es obligatorio.",
                        "Campo requerido",
                        JOptionPane.WARNING_MESSAGE);
                IdTextField.requestFocus();
                return;
            }

            int id;
            try {
                id = Integer.parseInt(IdTextField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "El ID debe ser un número válido.",
                        "ID inválido",
                        JOptionPane.ERROR_MESSAGE);
                IdTextField.requestFocus();
                return;
            }

            // Validar nombre
            String nombre = NombreTextField.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "El nombre es obligatorio.",
                        "Campo requerido",
                        JOptionPane.WARNING_MESSAGE);
                NombreTextField.requestFocus();
                return;
            }

            // Validar especialidad
            String especialidad = EspecialidadTextField.getText().trim();
            if (especialidad.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "La especialidad es obligatoria.",
                        "Campo requerido",
                        JOptionPane.WARNING_MESSAGE);
                EspecialidadTextField.requestFocus();
                return;
            }

            // Crear DTO y guardar
            MedicoDto dto = new MedicoDto(id, nombre, especialidad);
            controller.crearMedicoAsync(dto);
        });

        borrarButton.addActionListener(e -> {
            int fila = table.getSelectedRow();
            if (fila != -1) {
                int id = (int) table.getValueAt(fila, 0);
                String nombre = (String) table.getValueAt(fila, 1);

                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "¿Está seguro que desea eliminar el médico?\n\n" +
                                "ID: " + id + "\n" +
                                "Nombre: " + nombre,
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    controller.eliminarMedicoAsync(id);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Por favor seleccione un médico de la tabla.",
                        "Selección requerida",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        limpiarButton.addActionListener(e -> limpiarCampos());

        searchButton.addActionListener(e -> {
            String nombre = SearchNombreTextField.getText().trim();
            if (!nombre.isEmpty()) {
                controller.buscarMedicoAsync(nombre);
            } else {
                controller.listarMedicosAsync();
            }
        });

        // Listener para búsqueda al presionar Enter
        SearchNombreTextField.addActionListener(e -> {
            String nombre = SearchNombreTextField.getText().trim();
            if (!nombre.isEmpty()) {
                controller.buscarMedicoAsync(nombre);
            } else {
                controller.listarMedicosAsync();
            }
        });

        reporteButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Funcionalidad de reporte aún no implementada.",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE)
        );

        // Doble clic en tabla para editar
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = table.getSelectedRow();
                    if (fila != -1) {
                        cargarDatosEnFormulario(fila);
                    }
                }
            }
        });
    }

    private void cargarDatosEnFormulario(int fila) {
        IdTextField.setText(String.valueOf(table.getValueAt(fila, 0)));
        NombreTextField.setText((String) table.getValueAt(fila, 1));
        EspecialidadTextField.setText((String) table.getValueAt(fila, 2));
        IdTextField.setEnabled(false); // No permitir cambiar el ID
    }

    private void limpiarCampos() {
        IdTextField.setText("");
        NombreTextField.setText("");
        EspecialidadTextField.setText("");
        SearchNombreTextField.setText("");
        IdTextField.setEnabled(true);
        IdTextField.requestFocus();
    }

    @Override
    public void update(EventType eventType, Object data) {
        switch (eventType) {
            case UPDATED -> {
                if (data instanceof List) {
                    actualizarTabla((List<MedicoDto>) data);
                }
            }
            case CREATED -> {
                JOptionPane.showMessageDialog(this,
                        "Médico creado correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                controller.listarMedicosAsync();
            }
            case DELETED -> {
                JOptionPane.showMessageDialog(this,
                        "Médico eliminado correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                controller.listarMedicosAsync();
            }
        }
    }

    private void actualizarTabla(List<MedicoDto> medicos) {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Especialidad"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (MedicoDto m : medicos) {
            model.addRow(new Object[]{m.getId(), m.getNombre(), m.getEspecialidad()});
        }

        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ajustar ancho de columnas
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setPreferredWidth(80);   // ID
            table.getColumnModel().getColumn(1).setPreferredWidth(200);  // Nombre
            table.getColumnModel().getColumn(2).setPreferredWidth(150);  // Especialidad
        }
    }
}