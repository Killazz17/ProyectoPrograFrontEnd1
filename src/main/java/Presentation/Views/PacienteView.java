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

public class PacienteView extends JFrame implements IObserver {
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

        setupFrame();
        setupEvents();
        controller.listarPacientesAsync();
    }

    private void setupFrame() {
        setContentPane(ContentPanel);
        setTitle("Gestión de Pacientes");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
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
                JOptionPane.showMessageDialog(this, "ID inválido.");
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
                JOptionPane.showMessageDialog(this, "Funcionalidad de reporte aún no implementada.")
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
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Nombre", "Fecha Nacimiento", "Teléfono"}, 0);
        for (PacienteDto p : pacientes) {
            model.addRow(new Object[]{p.getId(), p.getNombre(), p.getFechaNacimiento(), p.getTelefono()});
        }
        table.setModel(model);
    }
}