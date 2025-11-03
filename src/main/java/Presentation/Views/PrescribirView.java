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

    // Variables para almacenar datos de la receta
    private PacienteDto pacienteSeleccionado = null;
    private List<MedicamentoPrescritoDto> medicamentosAgregados = new ArrayList<>();

    public PrescribirView() {
        controller = new PrescribirController(new PrescripcionService());
        controller.addObserver(this);

        // Configurar este JPanel con el contenido del form
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
                new Object[]{"Código", "Nombre", "Presentación", "Cantidad", "Duración (días)", "Indicaciones"}, 0
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
            // Obtener el JFrame padre
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

            // Abrir el diálogo de búsqueda
            BuscarPacienteView dialog = new BuscarPacienteView(parentFrame);
            dialog.setVisible(true);

            // Si se seleccionó un paciente, actualizar la vista
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
                System.out.println("[PrescribirView] Paciente seleccionado: " + pacienteSeleccionado);
            }
        } catch (Exception e) {
            System.err.println("[PrescribirView] Error al abrir búsqueda de paciente: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error al abrir búsqueda de paciente: " + e.getMessage(),
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
            // Obtener el JFrame padre
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

            // Abrir el diálogo de agregar medicamento
            AgregarMedicamentoView dialog = new AgregarMedicamentoView(parentFrame);
            dialog.setVisible(true);

            // Si se seleccionó un medicamento, agregarlo a la lista
            if (dialog.isSeleccionConfirmada()) {
                MedicamentoPrescritoDto medicamento = dialog.getMedicamentoSeleccionado();
                if (medicamento != null) {
                    medicamentosAgregados.add(medicamento);
                    actualizarTablaMedicamentos();
                    System.out.println("[PrescribirView] Medicamento agregado: " + medicamento);
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
        model.setRowCount(0); // Limpiar tabla

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
                    "¿Está seguro que desea eliminar este medicamento?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                medicamentosAgregados.remove(fila);
                actualizarTablaMedicamentos();
                System.out.println("[PrescribirView] Medicamento eliminado de la fila: " + fila);
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
        // Validaciones
        if (pacienteSeleccionado == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar un paciente",
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (medicamentosAgregados.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Debe agregar al menos un medicamento",
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            // Calcular fecha de retiro (7 días a partir de hoy por defecto)
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 7);
            Date fechaRetiro = cal.getTime();

            // Mostrar diálogo para confirmar o cambiar la fecha
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fechaStr = JOptionPane.showInputDialog(
                    this,
                    "Ingrese la fecha de retiro (dd/MM/yyyy):",
                    sdf.format(fechaRetiro)
            );

            if (fechaStr == null || fechaStr.trim().isEmpty()) {
                return; // Usuario canceló
            }

            // Parsear la fecha ingresada
            try {
                sdf.setLenient(false);
                fechaRetiro = sdf.parse(fechaStr.trim());
            } catch (java.text.ParseException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Formato de fecha inválido. Use dd/MM/yyyy (ejemplo: 31/12/2025)",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Validar que la fecha sea futura
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

            // Crear DTO de receta
            RecetaCreateDto receta = new RecetaCreateDto(
                    pacienteSeleccionado.getId(),
                    fechaRetiro,
                    new ArrayList<>(medicamentosAgregados)
            );

            System.out.println("[PrescribirView] === GUARDANDO RECETA ===");
            System.out.println("[PrescribirView] Paciente ID: " + pacienteSeleccionado.getId());
            System.out.println("[PrescribirView] Paciente Nombre: " + pacienteSeleccionado.getNombre());
            System.out.println("[PrescribirView] Fecha retiro: " + sdf.format(fechaRetiro));
            System.out.println("[PrescribirView] Medicamentos: " + medicamentosAgregados.size());
            for (int i = 0; i < medicamentosAgregados.size(); i++) {
                MedicamentoPrescritoDto med = medicamentosAgregados.get(i);
                System.out.println("[PrescribirView]   " + (i + 1) + ". " + med.getCodigo() +
                        " - Cant: " + med.getCantidad() + ", Dur: " + med.getDuracion() + " días");
            }
            System.out.println("[PrescribirView] ========================");

            // Enviar al controlador
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
                            "Código: %s\n" +
                            "Nombre: %s\n" +
                            "Presentación: %s\n" +
                            "Cantidad: %d unidades\n" +
                            "Duración: %d días\n" +
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
                            "✅ Receta creada correctamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    limpiarFormularioSinConfirmacion();
                }
                case DELETED -> {
                    JOptionPane.showMessageDialog(
                            this,
                            "Receta eliminada correctamente",
                            "Éxito",
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