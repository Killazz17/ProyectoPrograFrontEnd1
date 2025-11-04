package Presentation.Views;

import Domain.Dtos.PacienteDto;
import Presentation.Controllers.PacienteController;
import Presentation.IObserver;
import Services.PacienteService;
import Utilities.EventType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public PacienteView() {
        controller = new PacienteController(new PacienteService());
        controller.addObserver(this);

        setLayout(new java.awt.BorderLayout());

        if (ContentPanel == null) {
            createManualUI();
        } else {
            add(ContentPanel, java.awt.BorderLayout.CENTER);
        }

        setupEvents();
        controller.listarPacientesAsync();

        // Configurar colores de los campos de texto
        if (IdTextField != null) IdTextField.setForeground(Color.BLACK);
        if (NombreTextField != null) NombreTextField.setForeground(Color.BLACK);
        if (FechaNacimientoTextField != null) FechaNacimientoTextField.setForeground(Color.BLACK);
        if (TelefonoTextField != null) TelefonoTextField.setForeground(Color.BLACK);
        if (SearchNombreTextField != null) SearchNombreTextField.setForeground(Color.BLACK);

        dateFormat.setLenient(false);
    }

    private void createManualUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Paciente"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        IdTextField = new JTextField(15);
        IdTextField.setForeground(Color.BLACK);
        formPanel.add(IdTextField, gbc);

        // Nombre
        gbc.gridx = 2;
        formPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 3;
        NombreTextField = new JTextField(15);
        NombreTextField.setForeground(Color.BLACK);
        formPanel.add(NombreTextField, gbc);

        // Fecha de nacimiento
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Fecha (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        FechaNacimientoTextField = new JTextField(15);
        FechaNacimientoTextField.setForeground(Color.BLACK);
        formPanel.add(FechaNacimientoTextField, gbc);

        // Teléfono
        gbc.gridx = 2;
        formPanel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 3;
        TelefonoTextField = new JTextField(15);
        TelefonoTextField.setForeground(Color.BLACK);
        formPanel.add(TelefonoTextField, gbc);

        // Botones
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        JPanel btnPanel = new JPanel(new FlowLayout());
        guardarButton = new JButton("Guardar");
        limpiarButton = new JButton("Limpiar");
        borrarButton = new JButton("Borrar");
        btnPanel.add(guardarButton);
        btnPanel.add(limpiarButton);
        btnPanel.add(borrarButton);
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Búsqueda"));
        searchPanel.add(new JLabel("Nombre:"));
        SearchNombreTextField = new JTextField(20);
        SearchNombreTextField.setForeground(Color.BLACK);
        searchPanel.add(SearchNombreTextField);
        searchButton = new JButton("Buscar");
        searchPanel.add(searchButton);
        reporteButton = new JButton("Reporte");
        searchPanel.add(reporteButton);
        add(searchPanel, BorderLayout.CENTER);

        // Tabla
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Listado"));
        add(scrollPane, BorderLayout.SOUTH);

        setupEvents();
    }

    private void setupEvents() {
        if (guardarButton != null) {
            guardarButton.addActionListener(e -> guardarPaciente());
        }

        if (borrarButton != null) {
            borrarButton.addActionListener(e -> eliminarPaciente());
        }

        if (limpiarButton != null) {
            limpiarButton.addActionListener(e -> limpiarCampos());
        }

        if (searchButton != null) {
            searchButton.addActionListener(e -> buscarPaciente());
        }

        if (reporteButton != null) {
            reporteButton.addActionListener(e ->
                    JOptionPane.showMessageDialog(this, "Funcionalidad de reporte aún no implementada.")
            );
        }

        // Permitir selección de fila para editar/eliminar
        if (table != null) {
            table.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        cargarPacienteSeleccionado();
                    }
                }
            });
        }
    }

    private void guardarPaciente() {
        try {
            System.out.println("[PacienteView] Iniciando guardado de paciente...");

            // Validar ID
            String idStr = IdTextField.getText().trim();
            if (idStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "El ID es obligatorio.",
                        "Campo requerido",
                        JOptionPane.WARNING_MESSAGE);
                IdTextField.requestFocus();
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "El ID debe ser un número válido.",
                        "ID inválido",
                        JOptionPane.WARNING_MESSAGE);
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

            // Validar fecha
            String fechaStr = FechaNacimientoTextField.getText().trim();
            if (fechaStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "La fecha de nacimiento es obligatoria.",
                        "Campo requerido",
                        JOptionPane.WARNING_MESSAGE);
                FechaNacimientoTextField.requestFocus();
                return;
            }

            // Intentar parsear la fecha
            Date fechaNacimiento;
            try {
                fechaNacimiento = dateFormat.parse(fechaStr);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Formato de fecha inválido. Use YYYY-MM-DD\nEjemplo: 1990-12-25",
                        "Fecha inválida",
                        JOptionPane.WARNING_MESSAGE);
                FechaNacimientoTextField.requestFocus();
                return;
            }

            // Validar teléfono
            String telefono = TelefonoTextField.getText().trim();
            if (telefono.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "El teléfono es obligatorio.",
                        "Campo requerido",
                        JOptionPane.WARNING_MESSAGE);
                TelefonoTextField.requestFocus();
                return;
            }

            // Crear DTO y guardar
            PacienteDto dto = new PacienteDto(id, nombre, fechaStr, telefono);
            System.out.println("[PacienteView] DTO creado: ID=" + id + ", Nombre=" + nombre);

            controller.crearPacienteAsync(dto);
            System.out.println("[PacienteView] Solicitud de guardado enviada al controlador");

        } catch (Exception ex) {
            System.err.println("[PacienteView] Error al guardar paciente: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al guardar paciente: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarPaciente() {
        int fila = table.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un paciente de la tabla.",
                    "Selección requerida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = (int) table.getValueAt(fila, 0);
            String nombre = (String) table.getValueAt(fila, 1);

            System.out.println("[PacienteView] Intentando eliminar paciente ID: " + id);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar al paciente '" + nombre + "'?\n" +
                            "Esta acción no se puede deshacer.",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                controller.eliminarPacienteAsync(id);
                System.out.println("[PacienteView] Solicitud de eliminación enviada");
            }

        } catch (Exception ex) {
            System.err.println("[PacienteView] Error al eliminar paciente: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar paciente: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarPaciente() {
        String nombre = SearchNombreTextField.getText().trim();

        if (nombre.isEmpty()) {
            controller.listarPacientesAsync();
        } else {
            controller.buscarPacienteAsync(nombre);
        }
    }

    private void cargarPacienteSeleccionado() {
        int fila = table.getSelectedRow();

        if (fila != -1) {
            IdTextField.setText(String.valueOf(table.getValueAt(fila, 0)));
            NombreTextField.setText((String) table.getValueAt(fila, 1));

            // Formatear fecha
            Object fechaObj = table.getValueAt(fila, 2);
            if (fechaObj != null) {
                FechaNacimientoTextField.setText(fechaObj.toString());
            }

            TelefonoTextField.setText((String) table.getValueAt(fila, 3));
        }
    }

    private void limpiarCampos() {
        IdTextField.setText("");
        NombreTextField.setText("");
        FechaNacimientoTextField.setText("");
        TelefonoTextField.setText("");
        SearchNombreTextField.setText("");

        // Deseleccionar fila de la tabla
        if (table != null) {
            table.clearSelection();
        }

        IdTextField.requestFocus();
    }

    @Override
    public void update(EventType eventType, Object data) {
        switch (eventType) {
            case UPDATED:
                if (data instanceof List) {
                    actualizarTabla((List<PacienteDto>) data);
                }
                break;

            case CREATED:
                JOptionPane.showMessageDialog(this,
                        "Paciente creado correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                break;

            case DELETED:
                JOptionPane.showMessageDialog(this,
                        "Paciente eliminado correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                break;
        }
    }

    private void actualizarTabla(List<PacienteDto> pacientes) {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Fecha Nacimiento", "Teléfono"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (PacienteDto p : pacientes) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getNombre(),
                    p.getFechaNacimiento(),
                    p.getTelefono()
            });
        }

        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        System.out.println("[PacienteView] Tabla actualizada con " + pacientes.size() + " pacientes");
    }
}