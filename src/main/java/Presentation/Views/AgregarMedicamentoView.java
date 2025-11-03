package Presentation.Views;

import Domain.Dtos.MedicamentoDto;
import Domain.Dtos.MedicamentoPrescritoDto;
import Presentation.Controllers.MedicamentoController;
import Presentation.IObserver;
import Services.MedicamentoService;
import Utilities.EventType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AgregarMedicamentoView extends JDialog implements IObserver {
    private JPanel AddPillMainPanel;
    private JPanel MainPanel;
    private JLabel FilterByLabel;
    private JComboBox<String> FilterByComboBox;
    private JTextField FilterByTextField;
    private JScrollPane PillScrollPanel;
    private JTable AddPillTable;
    private JButton OkButton;
    private JButton CancelButton;

    private final MedicamentoController controller;
    private MedicamentoPrescritoDto medicamentoSeleccionado = null;
    private boolean seleccionConfirmada = false;

    public AgregarMedicamentoView(JFrame parent) {
        super(parent, "Agregar Medicamento", true);
        controller = new MedicamentoController(new MedicamentoService());
        controller.addObserver(this);

        initComponents();
        setupFrame();
        setupEvents();

        controller.listarMedicamentosAsync();
    }

    private void initComponents() {
        if (AddPillMainPanel == null) {
            createManualUI();
        } else {
            setContentPane(AddPillMainPanel);
        }
    }

    private void createManualUI() {
        MainPanel = new JPanel(new BorderLayout(10, 10));
        MainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior: Filtros
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        FilterByLabel = new JLabel("Filtrar por:");
        FilterByComboBox = new JComboBox<>(new String[]{"Nombre", "Código"});
        FilterByTextField = new JTextField(20);
        topPanel.add(FilterByLabel);
        topPanel.add(FilterByComboBox);
        topPanel.add(FilterByTextField);

        // Tabla
        AddPillTable = new JTable();
        PillScrollPanel = new JScrollPane(AddPillTable);

        // Panel inferior: Botones
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        OkButton = new JButton("Aceptar");
        try {
            OkButton.setIcon(new ImageIcon(getClass().getResource("/icons/check-mark.png")));
        } catch (Exception e) {
            // Si no hay icono, continuar sin él
        }
        CancelButton = new JButton("Cancelar");
        try {
            CancelButton.setIcon(new ImageIcon(getClass().getResource("/icons/close.png")));
        } catch (Exception e) {
            // Si no hay icono, continuar sin él
        }
        bottomPanel.add(OkButton);
        bottomPanel.add(CancelButton);

        MainPanel.add(topPanel, BorderLayout.NORTH);
        MainPanel.add(PillScrollPanel, BorderLayout.CENTER);
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
        FilterByTextField.addActionListener(e -> buscarMedicamento());

        // Doble clic en la tabla para seleccionar
        AddPillTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    seleccionarYConfigurar();
                }
            }
        });

        // Botón OK
        OkButton.addActionListener(e -> seleccionarYConfigurar());

        // Botón Cancelar
        CancelButton.addActionListener(e -> {
            seleccionConfirmada = false;
            medicamentoSeleccionado = null;
            dispose();
        });
    }

    private void buscarMedicamento() {
        String filtro = FilterByTextField.getText().trim();

        if (filtro.isEmpty()) {
            controller.listarMedicamentosAsync();
        } else {
            controller.buscarMedicamentoAsync(filtro);
        }
    }

    private void seleccionarYConfigurar() {
        int fila = AddPillTable.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor seleccione un medicamento de la tabla",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Obtener datos del medicamento seleccionado
        String codigo = (String) AddPillTable.getValueAt(fila, 0);
        String nombre = (String) AddPillTable.getValueAt(fila, 1);
        String presentacion = (String) AddPillTable.getValueAt(fila, 2);

        // Mostrar diálogo para configurar cantidad, duración e indicaciones
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Medicamento seleccionado:"));
        panel.add(new JLabel(nombre + " (" + presentacion + ")"));

        JSpinner cantidadSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        panel.add(new JLabel("Cantidad:"));
        panel.add(cantidadSpinner);

        JSpinner duracionSpinner = new JSpinner(new SpinnerNumberModel(7, 1, 365, 1));
        panel.add(new JLabel("Duración (días):"));
        panel.add(duracionSpinner);

        JTextField indicacionesField = new JTextField();
        panel.add(new JLabel("Indicaciones:"));
        panel.add(indicacionesField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Configurar Medicamento",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            int cantidad = (Integer) cantidadSpinner.getValue();
            int duracion = (Integer) duracionSpinner.getValue();
            String indicaciones = indicacionesField.getText().trim();

            if (indicaciones.isEmpty()) {
                indicaciones = "Tomar según indicaciones médicas";
            }

            // Crear el DTO del medicamento prescrito
            medicamentoSeleccionado = new MedicamentoPrescritoDto(
                    codigo,
                    cantidad,
                    duracion,
                    indicaciones
            );

            // Guardar también el nombre para mostrarlo en la vista principal
            medicamentoSeleccionado.setNombre(nombre);
            medicamentoSeleccionado.setPresentacion(presentacion);

            seleccionConfirmada = true;
            dispose();
        }
    }

    @Override
    public void update(EventType eventType, Object data) {
        if (eventType == EventType.UPDATED && data instanceof List) {
            actualizarTabla((List<MedicamentoDto>) data);
        }
    }

    private void actualizarTabla(List<MedicamentoDto> medicamentos) {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Código", "Nombre", "Presentación"}, 0
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

        AddPillTable.setModel(model);
        AddPillTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // Métodos públicos para obtener el resultado
    public MedicamentoPrescritoDto getMedicamentoSeleccionado() {
        return medicamentoSeleccionado;
    }

    public boolean isSeleccionConfirmada() {
        return seleccionConfirmada;
    }
}