package Presentation.Views;

import Domain.Dtos.RecetaDto;
import Presentation.Controllers.PacienteController;
import Presentation.Controllers.PrescribirController;
import Presentation.IObserver;
import Services.PrescripcionService;
import Utilities.EventType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class PrescribirView extends JPanel implements IObserver {
    private JPanel ContentPanel;
    private JPanel PrescribirMainPanel;
    private JPanel MainPanel;
    private JPanel ControlPanel;
    private JButton buscarPacienteButton;
    private JButton agregarMedicamentoButton;
    private JPanel PrescriptionPanel;
    private JLabel PatientLabel;
    private JTable table;
    private JPanel AdjustmentPanel;
    private JButton SaveButton;
    private JButton CleanButton;
    private JButton TrashMedButton;
    private JButton DetailsButton;
    private JPanel DatePanel;

    private final PrescribirController controller;

    public PrescribirView() {
        controller = new PrescribirController(new PrescripcionService());
        controller.addObserver(this);

        // Configurar este JPanel con el contenido del form
        setLayout(new java.awt.BorderLayout());
        if (ContentPanel != null) {
            add(ContentPanel, java.awt.BorderLayout.CENTER);
        }

        setupEvents();
        controller.listarRecetasAsync();
    }

    private void setupEvents() {
        buscarPacienteButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Funcionalidad de búsqueda de paciente aún no implementada.")
        );

        agregarMedicamentoButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Funcionalidad para agregar medicamentos aún no implementada.")
        );

        SaveButton.addActionListener(e -> {
            try {
                // Ejemplo mínimo para probar flujo
                RecetaDto dto = new RecetaDto(0, 1, 1, "2025-10-31", "ACTIVA", "Receta generada desde vista");
                controller.crearRecetaAsync(dto);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al crear receta: " + ex.getMessage());
            }
        });

        TrashMedButton.addActionListener(e -> {
            int fila = table.getSelectedRow();
            if (fila != -1) {
                int id = (int) table.getValueAt(fila, 0);
                controller.eliminarRecetaAsync(id);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione una receta.");
            }
        });

        CleanButton.addActionListener(e -> limpiarCampos());

        DetailsButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Funcionalidad de detalles aún no implementada.")
        );
    }

    private void limpiarCampos() {
        PatientLabel.setText("Paciente seleccionado: ---");
        if (table != null) table.clearSelection();
    }

    @Override
    public void update(EventType eventType, Object data) {
        switch (eventType) {
            case UPDATED -> actualizarTabla((List<RecetaDto>) data);
            case CREATED -> JOptionPane.showMessageDialog(this, "Receta creada correctamente.");
            case DELETED -> JOptionPane.showMessageDialog(this, "Receta eliminada correctamente.");
        }
    }

    private void actualizarTabla(List<RecetaDto> recetas) {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Paciente", "Médico", "Fecha", "Estado"}, 0
        );
        for (RecetaDto r : recetas) {
            model.addRow(new Object[]{r.getId(), r.getIdPaciente(), r.getIdMedico(), r.getFecha(), r.getEstado()});
        }
        table.setModel(model);
    }
}