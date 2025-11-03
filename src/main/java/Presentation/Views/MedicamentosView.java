package Presentation.Views;

import Domain.Dtos.MedicamentoDto;
import Presentation.Controllers.MedicamentoController;
import Presentation.IObserver;
import Services.MedicamentoService;
import Utilities.EventType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MedicamentosView extends JPanel implements IObserver {
    private JPanel ContentPanel;
    private JPanel FormPanel;
    private JPanel FormGroupPanel;
    private JPanel IdPanel;
    private JLabel CodigoLabel;
    private JTextField CodigoTextField;
    private JPanel NombrePanel;
    private JLabel NombreLabel;
    private JTextField NombreTextField;
    private JPanel FechaNacimientoPanel;
    private JLabel DescripcionLabel;
    private JTextField DescripcionTextField;
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

    private final MedicamentoController controller;

    public MedicamentosView() {
        controller = new MedicamentoController(new MedicamentoService());
        controller.addObserver(this);

        setLayout(new java.awt.BorderLayout());

        if (ContentPanel == null) {
            createManualUI();
        } else {
            add(ContentPanel, java.awt.BorderLayout.CENTER);
        }

        setupEvents();
        controller.listarMedicamentosAsync();

        // Configurar colores de los campos de texto
        if (CodigoTextField != null) CodigoTextField.setForeground(Color.BLACK);
        if (NombreTextField != null) NombreTextField.setForeground(Color.BLACK);
        if (DescripcionTextField != null) DescripcionTextField.setForeground(Color.BLACK);
        if (SearchNombreTextField != null) SearchNombreTextField.setForeground(Color.BLACK);
    }

    private void createManualUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Medicamento"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Código
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1;
        CodigoTextField = new JTextField(15);
        CodigoTextField.setForeground(Color.BLACK);
        formPanel.add(CodigoTextField, gbc);

        // Nombre
        gbc.gridx = 2;
        formPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 3;
        NombreTextField = new JTextField(15);
        NombreTextField.setForeground(Color.BLACK);
        formPanel.add(NombreTextField, gbc);

        // Descripción/Presentación
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Presentación:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        DescripcionTextField = new JTextField(35);
        DescripcionTextField.setForeground(Color.BLACK);
        formPanel.add(DescripcionTextField, gbc);

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
            guardarButton.addActionListener(e -> guardarMedicamento());
        }

        if (borrarButton != null) {
            borrarButton.addActionListener(e -> eliminarMedicamento());
        }

        if (limpiarButton != null) {
            limpiarButton.addActionListener(e -> limpiarCampos());
        }

        if (searchButton != null) {
            searchButton.addActionListener(e -> buscarMedicamento());
        }

        if (reporteButton != null) {
            reporteButton.addActionListener(e ->
                    JOptionPane.showMessageDialog(this, "Funcionalidad de reporte aún no implementada.")
            );
        }

        // Permitir selección de fila para eliminar
        if (table != null) {
            table.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        cargarMedicamentoSeleccionado();
                    }
                }
            });
        }
    }

    private void guardarMedicamento() {
        try {
            System.out.println("[MedicamentoView] Iniciando guardado de medicamento...");

            // Obtener y validar código
            String codigo = CodigoTextField.getText().trim();
            System.out.println("[MedicamentoView] Código ingresado: '" + codigo + "'");

            if (codigo.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "El código es obligatorio.",
                        "Campo requerido",
                        JOptionPane.WARNING_MESSAGE);
                CodigoTextField.requestFocus();
                return;
            }

            // Obtener y validar nombre
            String nombre = NombreTextField.getText().trim();
            System.out.println("[MedicamentoView] Nombre ingresado: '" + nombre + "'");

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "El nombre es obligatorio.",
                        "Campo requerido",
                        JOptionPane.WARNING_MESSAGE);
                NombreTextField.requestFocus();
                return;
            }

            // Obtener y validar descripción
            String descripcion = DescripcionTextField.getText().trim();
            System.out.println("[MedicamentoView] Descripción ingresada: '" + descripcion + "'");

            if (descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "La presentación es obligatoria.",
                        "Campo requerido",
                        JOptionPane.WARNING_MESSAGE);
                DescripcionTextField.requestFocus();
                return;
            }

            // Crear DTO y guardar
            MedicamentoDto dto = new MedicamentoDto(codigo, nombre, descripcion);
            System.out.println("[MedicamentoView] DTO creado: " + dto);

            controller.crearMedicamentoAsync(dto);
            System.out.println("[MedicamentoView] Solicitud de guardado enviada al controlador");

        } catch (Exception ex) {
            System.err.println("[MedicamentoView] Error al guardar medicamento: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al guardar medicamento: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarMedicamento() {
        int fila = table.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un medicamento de la tabla.",
                    "Selección requerida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String codigo = (String) table.getValueAt(fila, 0);
            String nombre = (String) table.getValueAt(fila, 1);

            System.out.println("[MedicamentoView] Intentando eliminar medicamento: " + codigo);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar el medicamento '" + nombre + "'?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                controller.eliminarMedicamentoAsync(codigo);
                System.out.println("[MedicamentoView] Solicitud de eliminación enviada");
            }

        } catch (Exception ex) {
            System.err.println("[MedicamentoView] Error al eliminar medicamento: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar medicamento: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarMedicamento() {
        String nombre = SearchNombreTextField.getText().trim();

        if (nombre.isEmpty()) {
            controller.listarMedicamentosAsync();
        } else {
            controller.buscarMedicamentoAsync(nombre);
        }
    }

    private void cargarMedicamentoSeleccionado() {
        int fila = table.getSelectedRow();

        if (fila != -1) {
            CodigoTextField.setText((String) table.getValueAt(fila, 0));
            NombreTextField.setText((String) table.getValueAt(fila, 1));
            DescripcionTextField.setText((String) table.getValueAt(fila, 2));
        }
    }

    private void limpiarCampos() {
        CodigoTextField.setText("");
        NombreTextField.setText("");
        DescripcionTextField.setText("");
        SearchNombreTextField.setText("");

        // Deseleccionar fila de la tabla
        if (table != null) {
            table.clearSelection();
        }

        CodigoTextField.requestFocus();
    }

    @Override
    public void update(EventType eventType, Object data) {
        switch (eventType) {
            case UPDATED:
                if (data instanceof List) {
                    actualizarTabla((List<MedicamentoDto>) data);
                }
                break;

            case CREATED:
                JOptionPane.showMessageDialog(this,
                        "Medicamento creado correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                break;

            case DELETED:
                JOptionPane.showMessageDialog(this,
                        "Medicamento eliminado correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                break;
        }
    }

    private void actualizarTabla(List<MedicamentoDto> medicamentos) {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Código", "Nombre", "Presentación"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (MedicamentoDto m : medicamentos) {
            model.addRow(new Object[]{
                    m.getCodigo(),
                    m.getNombre(),
                    m.getDescripcion()
            });
        }

        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        System.out.println("[MedicamentoView] Tabla actualizada con " + medicamentos.size() + " medicamentos");
    }
}