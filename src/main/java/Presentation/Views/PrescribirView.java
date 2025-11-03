package Presentation.Views;

import Domain.Dtos.MedicamentoPrescritoDto;
import Domain.Dtos.PacienteDto;
import Domain.Dtos.RecetaCreateDto;
import Domain.Dtos.RecetaDto;
import Presentation.Controllers.PrescribirController;
import Presentation.IObserver;
import Services.PrescripcionService;
import Utilities.EventType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    private PacienteDto pacienteSeleccionado = null;
    private List<MedicamentoPrescritoDto> medicamentosAgregados = new ArrayList<>();

    public PrescribirView() {
        controller = new PrescribirController(new PrescripcionService());
        controller.addObserver(this);

        setLayout(new java.awt.BorderLayout());
        if (ContentPanel != null) {
            add(ContentPanel, java.awt.BorderLayout.CENTER);
        }

        setupEvents();
        inicializarTabla();
        if (PatientLabel != null) {
            PatientLabel.setText("Seleccione un paciente");
        }
    }

    private void setupEvents() {
        if (buscarPacienteButton != null) {
            buscarPacienteButton.addActionListener(e -> abrirBuscarPaciente());
        }

        if (agregarMedicamentoButton != null) {
            agregarMedicamentoButton.addActionListener(e -> abrirAgregarMedicamento());
        }

        if (SaveButton != null) {
            SaveButton.addActionListener(e -> guardarReceta());
        }

        if (TrashMedButton != null) {
            TrashMedButton.addActionListener(e -> eliminarMedicamentoSeleccionado());
        }

        if (CleanButton != null) {
            CleanButton.addActionListener(e -> limpiarFormulario());
        }

        if (DetailsButton != null) {
            DetailsButton.addActionListener(e -> mostrarDetalles());
        }
    }

    private void inicializarTabla() {
        if (table == null) return;

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Codigo", "Nombre", "Presentacion", "Cantidad", "Duracion (dias)", "Indicaciones"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(model);
    }

    private void abrirBuscarPaciente() {
        try {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

            BuscarPacienteView dialog = new BuscarPacienteView(parentFrame);
            dialog.setVisible(true);

            if (dialog.isSeleccionConfirmada()) {
                pacienteSeleccionado = dialog.getPacienteSeleccionado();
                if (PatientLabel != null && pacienteSeleccionado != null) {
                    PatientLabel.setText(String.format(
                            "<html><b>Paciente:</b> %s (ID: %d)<br><b>Tel:</b> %s</html>",
                            pacienteSeleccionado.getNombre(),
                            pacienteSeleccionado.getId(),
                            pacienteSeleccionado.getTelefono()
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("[PrescribirView] Error al abrir búsqueda de paciente: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error al abrir busqueda de paciente: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void abrirAgregarMedicamento() {
        if (pacienteSeleccionado == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero debe seleccionar un paciente",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

            AgregarMedicamentoView dialog = new AgregarMedicamentoView(parentFrame);
            dialog.setVisible(true);

            if (dialog.isSeleccionConfirmada()) {
                MedicamentoPrescritoDto medicamento = dialog.getMedicamentoSeleccionado();
                if (medicamento != null) {
                    medicamentosAgregados.add(medicamento);
                    actualizarTablaMedicamentos();
                }
            }
        } catch (Exception e) {
            System.err.println("[PrescribirView] Error al abrir agregar medicamento: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error al abrir agregar medicamento: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void actualizarTablaMedicamentos() {
        if (table == null) return;

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (MedicamentoPrescritoDto med : medicamentosAgregados) {
            model.addRow(new Object[]{
                    med.getCodigo(),
                    med.getNombre() != null ? med.getNombre() : "N/A",
                    med.getPresentacion() != null ? med.getPresentacion() : "N/A",
                    med.getCantidad(),
                    med.getDuracion(),
                    med.getIndicaciones()
            });
        }
    }

    private void eliminarMedicamentoSeleccionado() {
        if (table == null) return;

        int fila = table.getSelectedRow();
        if (fila != -1) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "¿Esta seguro que desea eliminar este medicamento?",
                    "Confirmar eliminacion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                medicamentosAgregados.remove(fila);
                actualizarTablaMedicamentos();
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un medicamento de la tabla",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void guardarReceta() {
        if (pacienteSeleccionado == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar un paciente",
                    "Error de Validacion",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (medicamentosAgregados.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Debe agregar al menos un medicamento",
                    "Error de Validacion",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 7);
            Date fechaRetiro = cal.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fechaStr = JOptionPane.showInputDialog(
                    this,
                    "Ingrese la fecha de retiro (dd/MM/yyyy):",
                    sdf.format(fechaRetiro)
            );

            if (fechaStr == null || fechaStr.trim().isEmpty()) {
                return;
            }

                try {
                sdf.setLenient(false);
                fechaRetiro = sdf.parse(fechaStr.trim());
            } catch (java.text.ParseException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Formato de fecha invalido. Use dd/MM/yyyy (ejemplo: 31/12/2025)",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (fechaRetiro.before(new Date())) {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "La fecha de retiro es anterior a hoy. ¿Desea continuar?",
                        "Advertencia",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            RecetaCreateDto receta = new RecetaCreateDto(
                    pacienteSeleccionado.getId(),
                    fechaRetiro,
                    new ArrayList<>(medicamentosAgregados)
            );

            for (int i = 0; i < medicamentosAgregados.size(); i++) {
                MedicamentoPrescritoDto med = medicamentosAgregados.get(i);
            }

            controller.crearRecetaAsync(receta);

        } catch (Exception e) {
            System.err.println("[PrescribirView] Error al crear receta: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error al crear receta:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void limpiarFormulario() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea limpiar el formulario?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            pacienteSeleccionado = null;
            medicamentosAgregados.clear();

            if (PatientLabel != null) {
                PatientLabel.setText("Seleccione un paciente");
            }

            if (table != null) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);
            }

            System.out.println("[PrescribirView] Formulario limpiado");
        }
    }

    private void mostrarDetalles() {
        if (table == null) return;

        int fila = table.getSelectedRow();
        if (fila != -1) {
            MedicamentoPrescritoDto med = medicamentosAgregados.get(fila);
            String detalles = String.format(
                    "DETALLES DEL MEDICAMENTO\n\n" +
                            "Codigo: %s\n" +
                            "Nombre: %s\n" +
                            "Presentación: %s\n" +
                            "Cantidad: %d unidades\n" +
                            "Duracion: %d días\n" +
                            "Indicaciones:\n%s",
                    med.getCodigo(),
                    med.getNombre() != null ? med.getNombre() : "N/A",
                    med.getPresentacion() != null ? med.getPresentacion() : "N/A",
                    med.getCantidad(),
                    med.getDuracion(),
                    med.getIndicaciones()
            );

            JTextArea textArea = new JTextArea(detalles);
            textArea.setEditable(false);
            textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));

            JOptionPane.showMessageDialog(
                    this,
                    new JScrollPane(textArea),
                    "Detalles del Medicamento",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un medicamento de la tabla",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    @Override
    public void update(EventType eventType, Object data) {
        SwingUtilities.invokeLater(() -> {
            switch (eventType) {
                case UPDATED -> {
                    if (data instanceof List) {
                        actualizarTablaRecetas((List<RecetaDto>) data);
                    }
                }
                case CREATED -> {
                    JOptionPane.showMessageDialog(
                            this,
                            "Receta creada correctamente",
                            "Exito",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    limpiarFormularioSinConfirmacion();
                }
                case DELETED -> {
                    JOptionPane.showMessageDialog(
                            this,
                            "Receta eliminada correctamente",
                            "Exito",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });
    }

    private void limpiarFormularioSinConfirmacion() {
        pacienteSeleccionado = null;
        medicamentosAgregados.clear();

        if (PatientLabel != null) {
            PatientLabel.setText("Seleccione un paciente");
        }

        if (table != null) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
        }
    }

    private void actualizarTablaRecetas(List<RecetaDto> recetas) {
        System.out.println("[PrescribirView] Recetas recibidas: " + (recetas != null ? recetas.size() : 0));
    }
}