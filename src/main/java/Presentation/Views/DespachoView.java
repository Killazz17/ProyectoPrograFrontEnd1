package Presentation.Views;

import Domain.Dtos.DespachoDto;
import Presentation.Controllers.DespachoController;
import Presentation.IObserver;
import Services.DespachoService;
import Utilities.EventType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class DespachoView extends JFrame implements IObserver {

    private JPanel ContentPanel;        // ✅ agregado como root
    private JPanel MainContentPanel;
    private JPanel ButtonPanel;
    private JButton enProcesoButton;
    private JButton listaButton;
    private JButton recibirButton;
    private JPanel BuscarPanel;
    private JTextField idField;
    private JLabel idLabel;
    private JScrollPane RecetaTablePanel;
    private JTable RecetasTable;

    private final DespachoController controller;

    public DespachoView() {
        controller = new DespachoController(new DespachoService());
        controller.addObserver(this);

        setupFrame();
        setupEvents();
        controller.listarDespachosAsync();
    }

    private void setupFrame() {
        // ✅ usar ContentPanel como root (el que genera el GUI Designer)
        setContentPane(ContentPanel);
        setTitle("Despacho de Recetas");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void setupEvents() {
        enProcesoButton.addActionListener(e -> cambiarEstadoSeleccionado("EN_PROCESO"));
        listaButton.addActionListener(e -> cambiarEstadoSeleccionado("LISTA"));
        recibirButton.addActionListener(e -> cambiarEstadoSeleccionado("ENTREGADA"));

        idField.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                controller.buscarDespachoPorIdAsync(id);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID inválido");
            }
        });
    }

    private void cambiarEstadoSeleccionado(String nuevoEstado) {
        int fila = RecetasTable.getSelectedRow();
        if (fila != -1) {
            int id = (int) RecetasTable.getValueAt(fila, 0);
            controller.actualizarEstadoAsync(id, nuevoEstado);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un despacho.");
        }
    }

    @Override
    public void update(EventType eventType, Object data) {
        if (eventType == EventType.UPDATED) {
            actualizarTabla((List<DespachoDto>) data);
        }
    }

    private void actualizarTabla(List<DespachoDto> despachos) {
        if (despachos == null) return;
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Receta", "Estado"}, 0);
        for (DespachoDto d : despachos) {
            model.addRow(new Object[]{d.getId(), d.getIdReceta(), d.getEstado()});
        }
        RecetasTable.setModel(model);
    }
}