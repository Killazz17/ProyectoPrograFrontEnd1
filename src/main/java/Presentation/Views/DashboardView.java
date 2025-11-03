package Presentation.Views;

import Domain.Dtos.MedicamentoPrescritoDetalladoDto;
import Presentation.Controllers.DashboardController;
import Presentation.IObserver;
import Presentation.Models.RecetaLineChartModel;
import Presentation.Models.RecetaPieChartModel;
import Services.MedicamentoPrescritoService;
import Utilities.EventType;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class DashboardView extends JPanel implements IObserver {
    private JPanel MainPanel;
    private JFreeChart lineChart;
    private ChartPanel lineChartPanel;
    private RecetaLineChartModel lineModel;

    private JFreeChart pieChart;
    private ChartPanel pieChartPanel;
    private RecetaPieChartModel pieModel;

    private JComboBox<String> medicamentoCombo;
    private JComboBox<Integer> desdeAnio;
    private JComboBox<String> desdeMes;
    private JComboBox<Integer> hastaAnio;
    private JComboBox<String> hastaMes;
    private JButton filtrarButton;

    private List<MedicamentoPrescritoDetalladoDto> medicamentosActuales;
    private final DashboardController controller;

    public DashboardView() {
        MedicamentoPrescritoService service = new MedicamentoPrescritoService();
        controller = new DashboardController(service);
        controller.addObserver(this);

        medicamentosActuales = new ArrayList<>();
        MainPanel = new JPanel(new BorderLayout());

        // Crear modelos
        lineModel = new RecetaLineChartModel();
        pieModel = new RecetaPieChartModel();

        // Crear gráficos
        lineChart = ChartFactory.createLineChart(
                "Medicamentos Prescritos por Mes",
                "Mes",
                "Cantidad",
                lineModel
        );
        lineChartPanel = new ChartPanel(lineChart);

        pieChart = ChartFactory.createPieChart(
                "Recetas por Estado",
                pieModel,
                true, true, false
        );
        pieChartPanel = new ChartPanel(pieChart);

        // Panel dividido para los dos gráficos
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                lineChartPanel, pieChartPanel);
        splitPane.setDividerLocation(650);
        MainPanel.add(splitPane, BorderLayout.CENTER);

        // Panel de controles
        crearPanelControles();

        // Configurar este JPanel
        setLayout(new BorderLayout());
        add(MainPanel, BorderLayout.CENTER);

        // Cargar datos iniciales
        controller.cargarDatosAsync();
    }

    private void crearPanelControles() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Combo de medicamentos
        medicamentoCombo = new JComboBox<>();
        medicamentoCombo.addItem("-- Seleccione medicamento --");

        // Combos de año
        int anioActual = LocalDate.now().getYear();
        Integer[] anios = new Integer[10];
        for (int i = 0; i < 10; i++) {
            anios[i] = anioActual - 5 + i;
        }

        desdeAnio = new JComboBox<>(anios);
        hastaAnio = new JComboBox<>(anios);
        desdeAnio.setSelectedItem(anioActual);
        hastaAnio.setSelectedItem(anioActual);

        // Combos de mes
        String[] meses = {
                "01-Enero", "02-Febrero", "03-Marzo", "04-Abril", "05-Mayo", "06-Junio",
                "07-Julio", "08-Agosto", "09-Septiembre", "10-Octubre", "11-Noviembre",
                "12-Diciembre"
        };

        desdeMes = new JComboBox<>(meses);
        hastaMes = new JComboBox<>(meses);
        desdeMes.setSelectedIndex(0); // Enero
        hastaMes.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        // Botón filtrar
        filtrarButton = new JButton("✔ Filtrar");
        filtrarButton.addActionListener(e -> aplicarFiltros());

        // Agregar listeners para validación en tiempo real
        desdeAnio.addActionListener(e -> validarRangoFechas());
        desdeMes.addActionListener(e -> validarRangoFechas());
        hastaAnio.addActionListener(e -> validarRangoFechas());
        hastaMes.addActionListener(e -> validarRangoFechas());

        // Agregar componentes
        topPanel.add(new JLabel("Desde:"));
        topPanel.add(desdeAnio);
        topPanel.add(desdeMes);
        topPanel.add(new JLabel("Hasta:"));
        topPanel.add(hastaAnio);
        topPanel.add(hastaMes);
        topPanel.add(new JLabel("Medicamento:"));
        topPanel.add(medicamentoCombo);
        topPanel.add(filtrarButton);

        MainPanel.add(topPanel, BorderLayout.NORTH);
    }

    /**
     * Valida que el rango de fechas sea correcto en tiempo real
     * Deshabilita el botón de filtrar si las fechas son inválidas
     */
    private void validarRangoFechas() {
        try {
            int anioInicio = (Integer) desdeAnio.getSelectedItem();
            int mesInicio = desdeMes.getSelectedIndex() + 1;
            int anioFin = (Integer) hastaAnio.getSelectedItem();
            int mesFin = hastaMes.getSelectedIndex() + 1;

            LocalDate inicio = LocalDate.of(anioInicio, mesInicio, 1);
            LocalDate fin = LocalDate.of(anioFin, mesFin, 1);

            // Habilitar/deshabilitar botón según validez del rango
            if (inicio.isAfter(fin)) {
                filtrarButton.setEnabled(false);
                filtrarButton.setToolTipText("La fecha 'Desde' no puede ser posterior a 'Hasta'");
            } else {
                filtrarButton.setEnabled(true);
                filtrarButton.setToolTipText("Aplicar filtros");
            }
        } catch (Exception e) {
            filtrarButton.setEnabled(false);
        }
    }

    private void aplicarFiltros() {
        String medicamentoSeleccionado = (String) medicamentoCombo.getSelectedItem();

        if (medicamentoSeleccionado == null || medicamentoSeleccionado.startsWith("--")) {
            JOptionPane.showMessageDialog(this, "Seleccione un medicamento");
            return;
        }

        int anioInicio = (Integer) desdeAnio.getSelectedItem();
        int mesInicio = desdeMes.getSelectedIndex() + 1;
        int anioFin = (Integer) hastaAnio.getSelectedItem();
        int mesFin = hastaMes.getSelectedIndex() + 1;

        LocalDate inicio = LocalDate.of(anioInicio, mesInicio, 1);
        LocalDate fin = LocalDate.of(anioFin, mesFin, 1).plusMonths(1).minusDays(1);

        // Validar que la fecha "Desde" no sea posterior a la fecha "Hasta"
        if (inicio.isAfter(fin)) {
            JOptionPane.showMessageDialog(
                    this,
                    "La fecha 'Desde' no puede ser posterior a la fecha 'Hasta'.\n" +
                            "Por favor, ajuste las fechas correctamente.",
                    "Error en rango de fechas",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Filtrar medicamentos por rango de fechas
        List<MedicamentoPrescritoDetalladoDto> filtrados = medicamentosActuales.stream()
                .filter(mp -> {
                    if (mp.getFechaConfeccion() == null) return false;
                    try {
                        LocalDate fecha = LocalDate.parse(mp.getFechaConfeccion());
                        return !fecha.isBefore(inicio) && !fecha.isAfter(fin);
                    } catch (Exception e) {
                        System.err.println("[DashboardView] Error parseando fecha: "
                                + e.getMessage());
                        return false;
                    }
                })
                .toList();

        System.out.println("[DashboardView] Medicamentos filtrados: " + filtrados.size()
                + " de " + medicamentosActuales.size());

        // Actualizar gráfico de línea
        lineModel.mapData(filtrados, medicamentoSeleccionado);
    }

    @Override
    public void update(EventType eventType, Object data) {
        if (eventType == EventType.UPDATED && data instanceof List) {
            actualizarDatos((List<MedicamentoPrescritoDetalladoDto>) data);
        }
    }

    private void actualizarDatos(List<MedicamentoPrescritoDetalladoDto> medicamentos) {
        System.out.println("[DashboardView] ====== ACTUALIZANDO DATOS ======");
        System.out.println("[DashboardView] Medicamentos prescritos recibidos: "
                + (medicamentos != null ? medicamentos.size() : "NULL"));

        this.medicamentosActuales = medicamentos;

        // Extraer nombres únicos de medicamentos
        Set<String> nombresMedicamentos = new TreeSet<>();

        if (medicamentos != null && !medicamentos.isEmpty()) {
            for (MedicamentoPrescritoDetalladoDto mp : medicamentos) {
                if (mp.getMedicamentoNombre() != null && !mp.getMedicamentoNombre().isBlank()) {
                    nombresMedicamentos.add(mp.getMedicamentoNombre());
                    System.out.println("[DashboardView] Medicamento encontrado: "
                            + mp.getMedicamentoNombre());
                }
            }
        }

        System.out.println("[DashboardView] Total medicamentos únicos encontrados: "
                + nombresMedicamentos.size());

        // Actualizar combo de medicamentos
        medicamentoCombo.removeAllItems();
        medicamentoCombo.addItem("-- Seleccione medicamento --");

        for (String nombre : nombresMedicamentos) {
            medicamentoCombo.addItem(nombre);
            System.out.println("[DashboardView] Agregado al combo: " + nombre);
        }

        // Actualizar gráfico de pastel (estados de recetas)
        // Crear DTOs temporales para el gráfico de pastel
        Map<String, Integer> estadoCounts = new HashMap<>();
        for (MedicamentoPrescritoDetalladoDto mp : medicamentos) {
            String estado = mp.getEstado() != null ? mp.getEstado() : "SIN_ESTADO";
            estadoCounts.put(estado, estadoCounts.getOrDefault(estado, 0) + 1);
        }

        pieModel.clear();
        for (Map.Entry<String, Integer> entry : estadoCounts.entrySet()) {
            pieModel.setValue(entry.getKey(), entry.getValue());
        }

        // Si hay medicamentos, seleccionar el primero automáticamente
        if (!nombresMedicamentos.isEmpty()) {
            System.out.println("[DashboardView] ✓ Seleccionando primer medicamento automáticamente");
            medicamentoCombo.setSelectedIndex(1); // Primer medicamento
            aplicarFiltros();
        } else {
            System.out.println("[DashboardView] ⚠️ No hay medicamentos para mostrar");
            JOptionPane.showMessageDialog(
                    this,
                    "No se encontraron medicamentos prescritos.\n" +
                            "Verifique que existan recetas con medicamentos en la base de datos.",
                    "Sin datos",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }

        System.out.println("[DashboardView] ====== ACTUALIZACIÓN COMPLETADA ======\n");
    }
}