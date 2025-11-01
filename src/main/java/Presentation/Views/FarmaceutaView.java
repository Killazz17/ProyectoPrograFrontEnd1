package Presentation.Views;

import Domain.Dtos.FarmaceutaDto;
import Presentation.Controllers.FarmaceutaController;
import Presentation.IObserver;
import Services.FarmaceutaService;
import Utilities.EventType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FarmaceutaView extends JFrame implements IObserver {

    private JPanel ContentPanel;
    private JPanel FormPanel;
    private JPanel FormGroupPanel;
    private JPanel IdPanel;
    private JLabel IdLabel;
    private JTextField IdTextField;
    private JPanel NombrePanel;
    private JLabel NombreLabel;
    private JTextField NombreTextField;
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

    private final FarmaceutaController controller;

    public FarmaceutaView() {
        controller = new FarmaceutaController(new FarmaceutaService());
        controller.addObserver(this);

        setupFrame();
        setupEvents();
        controller.listarFarmaceutasAsync();
    }

    private void setupFrame() {
        setContentPane(ContentPanel);
        setTitle("Gestión de Farmacéutas");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void setupEvents() {
        guardarButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(IdTextField.getText());
                String nombre = NombreTextField.getText().trim();

                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El nombre es obligatorio.");
                    return;
                }

                FarmaceutaDto dto = new FarmaceutaDto(id, nombre);
                controller.crearFarmaceutaAsync(dto);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID inválido.");
            }
        });

        borrarButton.addActionListener(e -> {
            int fila = table.getSelectedRow();
            if (fila != -1) {
                int id = (int) table.getValueAt(fila, 0);
                controller.eliminarFarmaceutaAsync(id);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un farmaceuta.");
            }
        });

        limpiarButton.addActionListener(e -> limpiarCampos());

        searchButton.addActionListener(e -> {
            String nombre = SearchNombreTextField.getText().trim();
            if (!nombre.isEmpty()) controller.buscarFarmaceutaAsync(nombre);
            else controller.listarFarmaceutasAsync();
        });

        reporteButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Funcionalidad de reporte aún no implementada.")
        );
    }

    private void limpiarCampos() {
        IdTextField.setText("");
        NombreTextField.setText("");
        SearchNombreTextField.setText("");
    }

    @Override
    public void update(EventType eventType, Object data) {
        switch (eventType) {
            case UPDATED -> actualizarTabla((List<FarmaceutaDto>) data);
            case CREATED -> JOptionPane.showMessageDialog(this, "Farmaceuta creado correctamente.");
            case DELETED -> JOptionPane.showMessageDialog(this, "Farmaceuta eliminado correctamente.");
        }
    }

    private void actualizarTabla(List<FarmaceutaDto> farmaceutas) {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Nombre"}, 0);
        for (FarmaceutaDto f : farmaceutas) {
            model.addRow(new Object[]{f.getId(), f.getNombre()});
        }
        table.setModel(model);
    }
}