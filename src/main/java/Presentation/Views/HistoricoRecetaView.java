package Presentation.Views;

import Domain.Dtos.HistoricoRecetaDto;
import Presentation.Controllers.HistoricoRecetaController;
import Presentation.IObserver;
import Services.HistoricoRecetaService;
import Utilities.EventType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistoricoRecetaView extends JPanel implements IObserver {
    private JPanel ContentPanel;
    private JPanel MainPanel;
    private JPanel HistoricoMainPanel;
    private JPanel SearchPanel;
    private JComboBox FilterHistoricoPaciente;
    private JTextField FilterHistoricoTextField;
    private JScrollPane RectaScrollPanel;
    private JTable RecetaTable;
    private JPanel DetailPanel;
    private JButton DetailButton;

    private final HistoricoRecetaController controller;

    public HistoricoRecetaView() {
        controller = new HistoricoRecetaController(new HistoricoRecetaService());
        controller.addObserver(this);

        // Configurar este JPanel con el contenido del form
        setLayout(new java.awt.BorderLayout());
        if (ContentPanel != null) {
            add(ContentPanel, java.awt.BorderLayout.CENTER);
        }

        setupEvents();
        controller.listarHistoricoAsync();

        FilterHistoricoTextField.setForeground(Color.BLACK);
    }

    private void setupEvents() {
        FilterHistoricoTextField.addActionListener(e -> {
            String filtro = FilterHistoricoTextField.getText().trim();
            if (!filtro.isEmpty()) controller.buscarHistoricoAsync(filtro);
            else controller.listarHistoricoAsync();
        });

        DetailButton.addActionListener(e -> {
            int fila = RecetaTable.getSelectedRow();
            if (fila != -1) {
                String info = "Receta #" + RecetaTable.getValueAt(fila, 0)
                        + "\nPaciente: " + RecetaTable.getValueAt(fila, 1)
                        + "\nMédico: " + RecetaTable.getValueAt(fila, 2)
                        + "\nFecha: " + RecetaTable.getValueAt(fila, 3)
                        + "\nEstado: " + RecetaTable.getValueAt(fila, 4);
                JOptionPane.showMessageDialog(this, info, "Detalle de Receta", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione una receta del historial.");
            }
        });
    }

    @Override
    public void update(EventType eventType, Object data) {
        if (eventType == EventType.UPDATED) {
            actualizarTabla((List<HistoricoRecetaDto>) data);
        }
    }

    private void actualizarTabla(List<HistoricoRecetaDto> recetas) {
        if (recetas == null) return;
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Paciente", "Médico", "Fecha", "Estado"}, 0
        );
        for (HistoricoRecetaDto r : recetas) {
            model.addRow(new Object[]{
                    r.getId(), r.getPaciente(), r.getMedico(), r.getFecha(), r.getEstado()
            });
        }
        RecetaTable.setModel(model);
    }
}