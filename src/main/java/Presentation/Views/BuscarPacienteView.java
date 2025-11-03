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

public class BuscarPacienteView extends JDialog implements IObserver {
    private JPanel SearchPatientMainPanel;
    private JPanel MainPanel;
    private JLabel FilterByLabel;
    private JComboBox<String> FilterByComboBox;
    private JTextField FilterByTexField;
    private JScrollPane SeacrhPatientScrollPanel;
    private JTable SearchPatientPanel;
    private JButton CancelButton;
    private JButton OKButton;

    private final PacienteController controller;
    private PacienteDto pacienteSeleccionado = null;
    private boolean seleccionConfirmada = false;

    public BuscarPacienteView(JFrame parent) {
        super(parent, "Buscar Paciente", true); // Modal dialog
        controller = new PacienteController(new PacienteService());
        controller.addObserver(this);

        initComponents();
        setupFrame();
        setupEvents();

        controller.listarPacientesAsync();
    }

    private void initComponents() {
        if (SearchPatientMainPanel == null) {
            createManualUI();
        } else {
            setContentPane(SearchPatientMainPanel);
        }
    }

    private void createManualUI() {
        MainPanel = new JPanel(new BorderLayout(10, 10));
        MainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior: Filtros
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        FilterByLabel = new JLabel("Filtrar por:");
        FilterByComboBox = new JComboBox<>(new String[]{"Nombre", "ID"});
        FilterByTexField = new JTextField(20);
        topPanel.add(FilterByLabel);
        topPanel.add(FilterByComboBox);
        topPanel.add(FilterByTexField);

        // Tabla
        SearchPatientPanel = new JTable();
        SeacrhPatientScrollPanel = new JScrollPane(SearchPatientPanel);

        // Panel inferior: Botones
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        OKButton = new JButton("Aceptar");
        OKButton.setIcon(new ImageIcon(getClass().getResource("/icons/check-mark.png")));
        CancelButton = new JButton("Cancelar");
        CancelButton.setIcon(new ImageIcon(getClass().getResource("/icons/close.png")));
        bottomPanel.add(OKButton);
        bottomPanel.add(CancelButton);

        MainPanel.add(topPanel, BorderLayout.NORTH);
        MainPanel.add(SeacrhPatientScrollPanel, BorderLayout.CENTER);
        MainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(MainPanel);
    }

    private void setupFrame() {
        setSize(700, 500);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void setupEvents() {
        // Búsqueda en tiempo real
        FilterByTexField.addActionListener(e -> buscarPaciente());

        // Doble clic en la tabla para seleccionar
        SearchPatientPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    seleccionarYCerrar();
                }
            }
        });

        // Botón OK
        OKButton.addActionListener(e -> seleccionarYCerrar());

        // Botón Cancelar
        CancelButton.addActionListener(e -> {
            seleccionConfirmada = false;
            pacienteSeleccionado = null;
            dispose();
        });
    }

    private void buscarPaciente() {
        String filtro = FilterByTexField.getText().trim();
        String tipoBusqueda = (String) FilterByComboBox.getSelectedItem();

        if (filtro.isEmpty()) {
            controller.listarPacientesAsync();
        } else {
            if ("ID".equals(tipoBusqueda)) {
                // Buscar por ID (implementar si es necesario)
                controller.listarPacientesAsync();
            } else {
                // Buscar por nombre
                controller.buscarPacienteAsync(filtro);
            }
        }
    }

    private void seleccionarYCerrar() {
        int fila = SearchPatientPanel.getSelectedRow();
        if (fila != -1) {
            int id = (int) SearchPatientPanel.getValueAt(fila, 0);
            String nombre = (String) SearchPatientPanel.getValueAt(fila, 1);
            String fechaNac = (String) SearchPatientPanel.getValueAt(fila, 2);
            String telefono = (String) SearchPatientPanel.getValueAt(fila, 3);

            pacienteSeleccionado = new PacienteDto(id, nombre, fechaNac, telefono);
            seleccionConfirmada = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor seleccione un paciente de la tabla",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    @Override
    public void update(EventType eventType, Object data) {
        if (eventType == EventType.UPDATED && data instanceof List) {
            actualizarTabla((List<PacienteDto>) data);
        }
    }

    private void actualizarTabla(List<PacienteDto> pacientes) {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Fecha Nacimiento", "Teléfono"}, 0
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

        SearchPatientPanel.setModel(model);
        SearchPatientPanel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // Métodos públicos para obtener el resultado
    public PacienteDto getPacienteSeleccionado() {
        return pacienteSeleccionado;
    }

    public boolean isSeleccionConfirmada() {
        return seleccionConfirmada;
    }
}