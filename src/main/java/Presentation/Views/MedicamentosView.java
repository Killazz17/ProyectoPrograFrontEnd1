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

public class MedicamentosView extends JFrame implements IObserver {

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

        setupFrame();
        setupEvents();
        controller.listarMedicamentosAsync();
    }

    private void setupFrame() {
        setContentPane(ContentPanel);
        setTitle("Catálogo de Medicamentos");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void setupEvents() {
        guardarButton.addActionListener(e -> {
            String codigo = CodigoTextField.getText().trim();
            String nombre = NombreTextField.getText().trim();
            String descripcion = DescripcionTextField.getText().trim();

            if (codigo.isEmpty() || nombre.isEmpty() || descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
                return;
            }

            MedicamentoDto dto = new MedicamentoDto(codigo, nombre, descripcion);
            controller.crearMedicamentoAsync(dto);
        });

        borrarButton.addActionListener(e -> {
            int fila = table.getSelectedRow();
            if (fila != -1) {
                String codigo = (String) table.getValueAt(fila, 0);
                controller.eliminarMedicamentoAsync(codigo);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un medicamento.");
            }
        });

        limpiarButton.addActionListener(e -> limpiarCampos());

        searchButton.addActionListener(e -> {
            String nombre = SearchNombreTextField.getText().trim();
            if (!nombre.isEmpty()) controller.buscarMedicamentoAsync(nombre);
            else controller.listarMedicamentosAsync();
        });

        reporteButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Funcionalidad de reporte aún no implementada.")
        );
    }

    private void limpiarCampos() {
        CodigoTextField.setText("");
        NombreTextField.setText("");
        DescripcionTextField.setText("");
        SearchNombreTextField.setText("");
    }

    @Override
    public void update(EventType eventType, Object data) {
        switch (eventType) {
            case UPDATED -> actualizarTabla((List<MedicamentoDto>) data);
            case CREATED -> JOptionPane.showMessageDialog(this, "Medicamento creado correctamente.");
            case DELETED -> JOptionPane.showMessageDialog(this, "Medicamento eliminado correctamente.");
        }
    }

    private void actualizarTabla(List<MedicamentoDto> medicamentos) {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Código", "Nombre", "Descripción"}, 0);
        for (MedicamentoDto m : medicamentos) {
            model.addRow(new Object[]{m.getCodigo(), m.getNombre(), m.getDescripcion()});
        }
        table.setModel(model);
    }
}