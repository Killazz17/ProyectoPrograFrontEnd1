package Presentation.Views;

import Domain.Dtos.DespachoDto;
import Domain.Dtos.LoginResponseDto;
import Presentation.Controllers.DespachoController;
import Presentation.IObserver;
import Services.DespachoService;
import Utilities.EventType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DespachoView extends JPanel implements IObserver {
    private JPanel ContentPanel;
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
    private LoginResponseDto usuarioActual;

    public DespachoView() {
        this(null);
    }

    public DespachoView(LoginResponseDto usuario) {
        this.usuarioActual = usuario;
        controller = new DespachoController(new DespachoService());
        controller.addObserver(this);

        // Configurar este JPanel con el contenido del form
        setLayout(new BorderLayout());

        if (ContentPanel != null) {
            add(ContentPanel, BorderLayout.CENTER);
        } else {
            createManualUI();
        }

        configurarPermisosPorRol();
        setupEvents();
        cargarDatos();

        if (idField != null) idField.setForeground(Color.BLACK);
    }

    private void createManualUI() {
        MainContentPanel = new JPanel(new BorderLayout(10, 10));
        MainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior: Búsqueda
        BuscarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        idLabel = new JLabel("Buscar por ID:");
        idField = new JTextField(15);
        BuscarPanel.add(idLabel);
        BuscarPanel.add(idField);
        MainContentPanel.add(BuscarPanel, BorderLayout.NORTH);

        // Panel central: Tabla
        RecetasTable = new JTable();
        RecetasTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        RecetaTablePanel = new JScrollPane(RecetasTable);
        MainContentPanel.add(RecetaTablePanel, BorderLayout.CENTER);

        // Panel inferior: Botones
        ButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        enProcesoButton = new JButton("En Proceso");
        enProcesoButton.setBackground(new Color(255, 200, 100));

        listaButton = new JButton("Lista");
        listaButton.setBackground(new Color(100, 200, 255));

        recibirButton = new JButton("Entregada");
        recibirButton.setBackground(new Color(100, 255, 100));

        ButtonPanel.add(enProcesoButton);
        ButtonPanel.add(listaButton);
        ButtonPanel.add(recibirButton);

        MainContentPanel.add(ButtonPanel, BorderLayout.SOUTH);

        add(MainContentPanel, BorderLayout.CENTER);
    }

    private void configurarPermisosPorRol() {
        if (usuarioActual == null) {
            return;
        }

        String rol = usuarioActual.getRol();
        System.out.println("[DespachoView] Configurando permisos para rol: " + rol);

        if ("PACIENTE".equalsIgnoreCase(rol)) {
            // Para pacientes: solo mostrar sus recetas y solo botón "Recibir"
            enProcesoButton.setEnabled(false);
            listaButton.setEnabled(false);
            recibirButton.setEnabled(true);

            // Configurar filtro por paciente (asumiendo que el ID del usuario es el ID del paciente)
            controller.setPacienteFilter(usuarioActual.getId());

            System.out.println("[DespachoView] Modo PACIENTE activado - Solo puede marcar como entregada");

        } else if ("FARMACEUTA".equalsIgnoreCase(rol) || "MEDICO".equalsIgnoreCase(rol) ||
                "ADMIN".equalsIgnoreCase(rol) || "ADMINISTRADOR".equalsIgnoreCase(rol)) {
            // Para farmacéutas, médicos y admins: todos los botones habilitados
            enProcesoButton.setEnabled(true);
            listaButton.setEnabled(true);
            recibirButton.setEnabled(true);

            controller.clearPacienteFilter();

            System.out.println("[DespachoView] Modo STAFF activado - Todos los botones habilitados");
        }
    }

    private void cargarDatos() {
        controller.listarDespachosAsync();
    }

    private void setupEvents() {
        if (enProcesoButton != null) {
            enProcesoButton.addActionListener(e -> cambiarEstadoSeleccionado("proceso"));
        }

        if (listaButton != null) {
            listaButton.addActionListener(e -> cambiarEstadoSeleccionado("lista"));
        }

        if (recibirButton != null) {
            recibirButton.addActionListener(e -> cambiarEstadoSeleccionado("entregada"));
        }

        if (idField != null) {
            idField.addActionListener(e -> {
                try {
                    String texto = idField.getText().trim();
                    if (texto.isEmpty()) {
                        cargarDatos();
                    } else {
                        int id = Integer.parseInt(texto);
                        controller.buscarDespachoPorIdAsync(id);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Por favor ingrese un ID válido",
                            "ID Inválido",
                            JOptionPane.WARNING_MESSAGE);
                }
            });
        }

        // Doble clic para ver detalles
        if (RecetasTable != null) {
            RecetasTable.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        mostrarDetalles();
                    }
                }
            });
        }
    }

    private void cambiarEstadoSeleccionado(String nuevoEstado) {
        if (RecetasTable == null) return;

        int fila = RecetasTable.getSelectedRow();
        if (fila != -1) {
            int id = (int) RecetasTable.getValueAt(fila, 0);
            String estadoActual = (String) RecetasTable.getValueAt(fila, 4);

            // Validar transición de estados
            if (!validarTransicionEstado(estadoActual, nuevoEstado)) {
                String mensajeError = construirMensajeError(estadoActual, nuevoEstado);
                JOptionPane.showMessageDialog(this,
                        mensajeError,
                        "Transición Inválida",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de cambiar el estado a " + nuevoEstado.toUpperCase() + "?",
                    "Confirmar Cambio",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                controller.actualizarEstadoAsync(id, nuevoEstado);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione una receta de la tabla.",
                    "Seleccionar Receta",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Validación estricta de transiciones de estado
     * Solo permite el flujo: confeccionada -> proceso -> lista -> entregada
     */
    private boolean validarTransicionEstado(String estadoActual, String nuevoEstado) {
        // Reglas de transición estrictas:
        // confeccionada -> proceso
        // proceso -> lista
        // lista -> entregada
        // entregada -> (no se puede cambiar)

        if ("entregada".equalsIgnoreCase(estadoActual)) {
            return false; // No se puede cambiar una receta entregada
        }

        // Normalizar estados a minúsculas para comparación
        String estadoActualLower = estadoActual.toLowerCase();
        String nuevoEstadoLower = nuevoEstado.toLowerCase();

        // Validar transiciones permitidas según el flujo
        switch (estadoActualLower) {
            case "confeccionada":
                // Solo puede pasar a proceso
                return "proceso".equals(nuevoEstadoLower);

            case "proceso":
                // Solo puede pasar a lista
                return "lista".equals(nuevoEstadoLower);

            case "lista":
                // Solo puede pasar a entregada
                return "entregada".equals(nuevoEstadoLower);

            default:
                return false; // Estado desconocido
        }
    }

    /**
     * Construye un mensaje de error informativo según el estado actual
     */
    private String construirMensajeError(String estadoActual, String nuevoEstado) {
        String estadoLower = estadoActual.toLowerCase();

        switch (estadoLower) {
            case "confeccionada":
                return "Las recetas CONFECCIONADAS solo pueden pasar a estado EN PROCESO.\n\n" +
                        "Flujo correcto: Confeccionada → Proceso → Lista → Entregada";

            case "proceso":
                return "Las recetas EN PROCESO solo pueden pasar a estado LISTA.\n\n" +
                        "Flujo correcto: Confeccionada → Proceso → Lista → Entregada";

            case "lista":
                return "Las recetas LISTAS solo pueden pasar a estado ENTREGADA.\n\n" +
                        "Flujo correcto: Confeccionada → Proceso → Lista → Entregada";

            case "entregada":
                return "Las recetas ENTREGADAS no pueden cambiar de estado.\n\n" +
                        "Una vez entregada, la receta está finalizada.";

            default:
                return "No se puede cambiar de " + estadoActual.toUpperCase() +
                        " a " + nuevoEstado.toUpperCase() + ".\n\n" +
                        "Flujo correcto: Confeccionada → Proceso → Lista → Entregada";
        }
    }

    private void mostrarDetalles() {
        if (RecetasTable == null) return;

        int fila = RecetasTable.getSelectedRow();
        if (fila != -1) {
            int id = (int) RecetasTable.getValueAt(fila, 0);
            String paciente = (String) RecetasTable.getValueAt(fila, 1);
            String fechaConf = (String) RecetasTable.getValueAt(fila, 2);
            String fechaRet = (String) RecetasTable.getValueAt(fila, 3);
            String estado = (String) RecetasTable.getValueAt(fila, 4);
            int cantMeds = (int) RecetasTable.getValueAt(fila, 5);

            String detalles = String.format(
                    "DETALLES DE LA RECETA\n\n" +
                            "ID: %d\n" +
                            "Paciente: %s\n" +
                            "Fecha Confección: %s\n" +
                            "Fecha Retiro: %s\n" +
                            "Estado: %s\n" +
                            "Cantidad Medicamentos: %d",
                    id, paciente, fechaConf, fechaRet, estado, cantMeds
            );

            JOptionPane.showMessageDialog(this,
                    detalles,
                    "Detalle de Receta #" + id,
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void update(EventType eventType, Object data) {
        if (eventType == EventType.UPDATED && data instanceof List) {
            actualizarTabla((List<DespachoDto>) data);
        }
    }

    private void actualizarTabla(List<DespachoDto> despachos) {
        if (despachos == null) return;

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Paciente", "F. Confección", "F. Retiro", "Estado", "# Meds"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (DespachoDto d : despachos) {
            model.addRow(new Object[]{
                    d.getId(),
                    d.getNombrePaciente(),
                    d.getFechaConfeccion(),
                    d.getFechaRetiro(),
                    d.getEstado().toUpperCase(),
                    d.getCantidadMedicamentos()
            });
        }

        RecetasTable.setModel(model);

        // Ajustar anchos de columna
        if (RecetasTable.getColumnModel().getColumnCount() > 0) {
            RecetasTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
            RecetasTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Paciente
            RecetasTable.getColumnModel().getColumn(2).setPreferredWidth(100); // F. Confección
            RecetasTable.getColumnModel().getColumn(3).setPreferredWidth(100); // F. Retiro
            RecetasTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Estado
            RecetasTable.getColumnModel().getColumn(5).setPreferredWidth(70);  // # Meds
        }

        System.out.println("[DespachoView] Tabla actualizada con " + despachos.size() + " recetas");
    }
}