package Presentation.Views;

import Domain.Dtos.ResponseDto;
import Presentation.Controllers.PacienteController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PacienteView extends JPanel {
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
    private DefaultTableModel tableModel;

    public PacienteView() {
        this.controller = new PacienteController();

        setLayout(new java.awt.BorderLayout());
        add(ContentPanel, java.awt.BorderLayout.CENTER);

        initializeTable();
        setupListeners();
        loadData();
    }

    private void initializeTable() {
        String[] columns = {"ID", "Nombre", "Fecha Nacimiento", "Teléfono"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(tableModel);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                loadSelectedRow();
            }
        });
    }

    private void setupListeners() {
        guardarButton.addActionListener(e -> saveData());
        limpiarButton.addActionListener(e -> clearFields());
        borrarButton.addActionListener(e -> deleteData());
        searchButton.addActionListener(e -> searchData());
        reporteButton.setEnabled(false); // Por implementar
    }

    private void loadData() {
        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Map<String, Object>> doInBackground() {
                return controller.loadAll();
            }

            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> pacientes = get();
                    if (pacientes != null) {
                        updateTable(pacientes);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PacienteView.this,
                            "Error al cargar pacientes: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void updateTable(List<Map<String, Object>> pacientes) {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Map<String, Object> p : pacientes) {
            Object[] row = {
                    p.get("id"),
                    p.get("nombre"),
                    p.get("fechaNacimiento") != null ?
                            sdf.format(new Date(((Number)p.get("fechaNacimiento")).longValue())) : "",
                    p.get("numeroTelefono")
            };
            tableModel.addRow(row);
        }
    }

    private void loadSelectedRow() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            IdTextField.setText(tableModel.getValueAt(row, 0).toString());
            NombreTextField.setText(tableModel.getValueAt(row, 1).toString());
            FechaNacimientoTextField.setText(tableModel.getValueAt(row, 2).toString());
            TelefonoTextField.setText(tableModel.getValueAt(row, 3).toString());
        }
    }

    private void saveData() {
        if (!validateFields()) return;

        int id = Integer.parseInt(IdTextField.getText().trim());
        String nombre = NombreTextField.getText().trim();
        String telefono = TelefonoTextField.getText().trim();

        Date fechaNacimiento;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            fechaNacimiento = sdf.parse(FechaNacimientoTextField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha inválido. Use dd/MM/yyyy",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ResponseDto response = controller.create(id, nombre, fechaNacimiento, telefono);

        if (response.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Paciente guardado correctamente");
            clearFields();
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteData() {
        if (IdTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente de la tabla");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar este paciente?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(IdTextField.getText().trim());
            ResponseDto response = controller.delete(id);

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Paciente eliminado");
                clearFields();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, response.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchData() {
        String searchTerm = SearchNombreTextField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadData();
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            String nombre = model.getValueAt(i, 1).toString().toLowerCase();
            if (!nombre.contains(searchTerm)) {
                model.removeRow(i);
            }
        }
    }

    private boolean validateFields() {
        if (IdTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El ID es requerido");
            return false;
        }
        if (NombreTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es requerido");
            return false;
        }
        if (FechaNacimientoTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La fecha de nacimiento es requerida");
            return false;
        }
        if (TelefonoTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El teléfono es requerido");
            return false;
        }
        return true;
    }

    private void clearFields() {
        IdTextField.setText("");
        NombreTextField.setText("");
        FechaNacimientoTextField.setText("");
        TelefonoTextField.setText("");
        table.clearSelection();
    }
}