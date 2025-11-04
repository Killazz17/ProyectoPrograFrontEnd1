package Presentation.Views;

import Domain.Dtos.HistoricoRecetaDto;
import Domain.Dtos.MedicamentoPrescritoDto;
import Domain.Dtos.RecetaDetalladaDto;
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
    private JComboBox<String> FilterHistoricoPaciente;
    private JTextField FilterHistoricoTextField;
    private JScrollPane RectaScrollPanel;
    private JTable RecetaTable;
    private JPanel DetailPanel;
    private JButton DetailButton;

    private final HistoricoRecetaController controller;
    private DefaultTableModel tableModel;

    public HistoricoRecetaView() {
        controller = new HistoricoRecetaController(new HistoricoRecetaService());
        controller.addObserver(this);

        setLayout(new BorderLayout());
        if (ContentPanel != null) {
            add(ContentPanel, BorderLayout.CENTER);
        }

        setupTable();
        setupComboBox();
        setupEvents();

        controller.listarHistoricoAsync();

        FilterHistoricoTextField.setForeground(Color.BLACK);
    }

    private void setupTable() {
        tableModel = new DefaultTableModel(
                new Object[]{"ID Receta", "Paciente (ID)", "Médico", "Fecha", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        RecetaTable.setModel(tableModel);
        RecetaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        RecetaTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        RecetaTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        RecetaTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        RecetaTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        RecetaTable.getColumnModel().getColumn(4).setPreferredWidth(100);
    }

    // CAMBIO: ComboBox ahora dice "ID Receta"
    private void setupComboBox() {
        FilterHistoricoPaciente.removeAllItems();
        FilterHistoricoPaciente.addItem("ID Receta");
        FilterHistoricoPaciente.addItem("Nombre Paciente");
        FilterHistoricoPaciente.setSelectedIndex(0);
    }

    private void setupEvents() {
        FilterHistoricoTextField.addActionListener(e -> realizarBusqueda());

        FilterHistoricoTextField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private Timer timer;

            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { buscarConDelay(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { buscarConDelay(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { buscarConDelay(); }

            private void buscarConDelay() {
                if (timer != null) timer.stop();
                timer = new Timer(150, evt -> realizarBusqueda());
                timer.setRepeats(false);
                timer.start();
            }
        });

        DetailButton.addActionListener(e -> mostrarDetalle());
        RecetaTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) mostrarDetalle();
            }
        });
    }

    // BÚSQUEDA: "ID Receta" → envía "id_receta"
    private void realizarBusqueda() {
        String filtro = FilterHistoricoTextField.getText().trim();
        if (filtro.isEmpty()) {
            controller.listarHistoricoAsync();
            return;
        }

        String tipo = (String) FilterHistoricoPaciente.getSelectedItem();

        if (tipo != null && tipo.equals("ID Receta")) {
            try {
                int id = Integer.parseInt(filtro);
                System.out.println("[HistoricoView] Buscando receta con ID: " + id);
                controller.buscarHistoricoAsync("id_receta", String.valueOf(id));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Por favor, ingrese un número válido para el ID de receta.",
                        "ID inválido",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("[HistoricoView] Buscando por nombre: '" + filtro + "'");
            controller.buscarHistoricoAsync("nombre", filtro);
        }
    }

    // EXTRAER NOMBRE PARA EL DETALLE
    private void mostrarDetalle() {
        int fila = RecetaTable.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una receta.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idReceta = (int) RecetaTable.getValueAt(fila, 0);
        String textoPaciente = (String) RecetaTable.getValueAt(fila, 1);
        String nombrePaciente = textoPaciente.split("\\(")[0].trim();

        controller.setNombrePacienteSeleccionado(nombrePaciente);
        controller.obtenerDetalleAsync(idReceta);
    }

    @Override
    public void update(EventType eventType, Object data) {
        SwingUtilities.invokeLater(() -> {
            switch (eventType) {
                case UPDATED:
                    actualizarTabla((List<HistoricoRecetaDto>) data);
                    break;
                case DETAIL_LOADED:
                    mostrarDialogoDetalle((RecetaDetalladaDto) data);
                    break;
                case ERROR:
                    JOptionPane.showMessageDialog(this, "Error: " + data, "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        });
    }

    private void actualizarTabla(List<HistoricoRecetaDto> recetas) {
        if (recetas == null) return;

        tableModel.setRowCount(0);
        for (HistoricoRecetaDto r : recetas) {
            tableModel.addRow(new Object[]{
                    r.getId(),  // ID REAL DE LA RECETA
                    r.getPaciente(),
                    r.getMedico(),
                    r.getFecha(),
                    formatearEstado(r.getEstado())
            });
        }
        System.out.println("[HistoricoView] Tabla actualizada: " + recetas.size() + " recetas");
    }

    private void mostrarDialogoDetalle(RecetaDetalladaDto receta) {
        if (receta == null) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar el detalle.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel info = new JPanel(new GridLayout(0, 2, 5, 5));
        info.setBorder(BorderFactory.createTitledBorder("Información General"));

        info.add(new JLabel("ID Receta:")); info.add(new JLabel(String.valueOf(receta.getId())));
        info.add(new JLabel("ID Paciente:")); info.add(new JLabel(String.valueOf(receta.getIdPaciente())));
        info.add(new JLabel("Paciente:")); info.add(new JLabel(receta.getPacienteNombre()));
        info.add(new JLabel("Fecha Confección:")); info.add(new JLabel(receta.getFechaConfeccion()));
        info.add(new JLabel("Fecha Retiro:")); info.add(new JLabel(receta.getFechaRetiro()));
        info.add(new JLabel("Estado:"));
        JLabel lblEstado = new JLabel(formatearEstado(receta.getEstado()));
        lblEstado.setFont(lblEstado.getFont().deriveFont(Font.BOLD));
        lblEstado.setForeground(obtenerColorEstado(receta.getEstado()));
        info.add(lblEstado);

        JPanel meds = new JPanel(new BorderLayout());
        meds.setBorder(BorderFactory.createTitledBorder("Medicamentos Prescritos"));

        String[] cols = {"Código", "Cantidad", "Duración (días)", "Indicaciones"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(model);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        for (MedicamentoPrescritoDto m : receta.getMedicamentos()) {
            model.addRow(new Object[]{ m.getCodigo(), m.getCantidad(), m.getDuracion(), m.getIndicaciones() });
        }

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(700, 200));
        meds.add(scroll, BorderLayout.CENTER);

        panel.add(info, BorderLayout.NORTH);
        panel.add(meds, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel,
                "Detalle de Receta #" + receta.getId(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private String formatearEstado(String estado) {
        if (estado == null) return "Desconocido";
        return switch (estado.toLowerCase()) {
            case "confeccionada" -> "Confeccionada";
            case "proceso" -> "En Proceso";
            case "lista" -> "Lista";
            case "entregada" -> "Entregada";
            default -> estado;
        };
    }

    private Color obtenerColorEstado(String estado) {
        if (estado == null) return Color.BLACK;
        return switch (estado.toLowerCase()) {
            case "confeccionada" -> new Color(255, 140, 0);
            case "proceso" -> new Color(0, 123, 255);
            case "lista" -> new Color(40, 167, 69);
            case "entregada" -> new Color(108, 117, 125);
            default -> Color.BLACK;
        };
    }
}