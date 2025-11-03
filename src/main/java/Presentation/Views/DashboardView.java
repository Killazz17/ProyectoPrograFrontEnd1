// src/main/java/Presentation/Views/DashboardView.java
package Presentation.Views;

import Domain.Dtos.MedicamentoPrescritoDto;
import Domain.Dtos.RecetaDetalladaDto;
import Presentation.Controllers.DashboardController;
import Presentation.IObserver;
import Presentation.Models.RecetaLineChartModel;
import Presentation.Models.RecetaPieChartModel;
import Services.RecetaService;
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

    private List<RecetaDetalladaDto> recetasActuales;
    private final DashboardController controller;

    public DashboardView() {
        RecetaService recetaService = new RecetaService();
        controller = new DashboardController(recetaService);
        controller.addObserver(this);

        recetasActuales = new ArrayList<>();
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
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, lineChartPanel, pieChartPanel);
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

        // Combo de medicamentos (se llenará cuando lleguen los datos)
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
                "07-Julio", "08-Agosto", "09-Septiembre", "10-Octubre", "11-Noviembre", "12-Diciembre"
        };

        desdeMes = new JComboBox<>(meses);
        hastaMes = new JComboBox<>(meses);
        desdeMes.setSelectedIndex(0); // Enero
        hastaMes.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        // Botón filtrar
        filtrarButton = new JButton("✔ Filtrar");
        filtrarButton.addActionListener(e -> aplicarFiltros());

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

        // ✅ FIX: Usar fechaConfeccion en lugar de fecha
        List<RecetaDetalladaDto> filtradas = recetasActuales.stream()
                .filter(r -> {
                    if (r.getFechaConfeccion() == null) return false;
                    try {
                        LocalDate fecha = LocalDate.parse(r.getFechaConfeccion());
                        return !fecha.isBefore(inicio) && !fecha.isAfter(fin);
                    } catch (Exception e) {
                        System.err.println("[DashboardView] Error parseando fecha: " + e.getMessage());
                        return false;
                    }
                })
                .toList();

        System.out.println("[DashboardView] Recetas filtradas: " + filtradas.size() + " de " + recetasActuales.size());

        // Actualizar gráfico de línea
        lineModel.mapData(filtradas, medicamentoSeleccionado);
    }

    @Override
    public void update(EventType eventType, Object data) {
        if (eventType == EventType.UPDATED && data instanceof List) {
            actualizarDatos((List<RecetaDetalladaDto>) data);
        }
    }

    // ✅ MÉTODO ACTUALIZADO en DashboardView.java
    private void actualizarDatos(List<RecetaDetalladaDto> recetas) {
        System.out.println("[DashboardView] ====== ACTUALIZANDO DATOS ======");
        System.out.println("[DashboardView] Recetas recibidas: " + (recetas != null ? recetas.size() : "NULL"));

        this.recetasActuales = recetas;

        // Extraer nombres únicos de medicamentos
        Set<String> nombresMedicamentos = new TreeSet<>();

        if (recetas != null && !recetas.isEmpty()) {
            for (RecetaDetalladaDto receta : recetas) {
                System.out.println("[DashboardView] Procesando receta ID: " + receta.getId());

                if (receta.getMedicamentos() != null) {
                    System.out.println("[DashboardView]   Medicamentos en receta: " +
                            receta.getMedicamentos().size());

                    for (MedicamentoPrescritoDto med : receta.getMedicamentos()) {
                        if (med.getNombre() != null && !med.getNombre().isBlank()) {
                            nombresMedicamentos.add(med.getNombre());
                            System.out.println("[DashboardView]     ✓ Agregado: " + med.getNombre());
                        } else {
                            System.out.println("[DashboardView]     ⚠️ Medicamento sin nombre: " +
                                    med.getCodigo());
                        }
                    }
                } else {
                    System.out.println("[DashboardView]   ⚠️ Lista de medicamentos NULL");
                }
            }
        }

        System.out.println("[DashboardView] Total medicamentos únicos encontrados: " +
                nombresMedicamentos.size());

        // Actualizar combo de medicamentos
        medicamentoCombo.removeAllItems();
        medicamentoCombo.addItem("-- Seleccione medicamento --");

        for (String nombre : nombresMedicamentos) {
            medicamentoCombo.addItem(nombre);
            System.out.println("[DashboardView]   Agregado al combo: " + nombre);
        }

        // Actualizar gráfico de pastel (no depende de filtros)
        pieModel.mapData(recetas);

        // Si hay medicamentos, seleccionar el primero y mostrar datos
        if (!nombresMedicamentos.isEmpty()) {
            System.out.println("[DashboardView] ✓ Seleccionando primer medicamento automáticamente");
            medicamentoCombo.setSelectedIndex(1); // Primer medicamento
            aplicarFiltros();
        } else {
            System.out.println("[DashboardView] ⚠️ No hay medicamentos para mostrar");
            JOptionPane.showMessageDialog(
                    this,
                    "No se encontraron medicamentos en las recetas.\n" +
                            "Verifique que existan recetas con medicamentos en la base de datos.",
                    "Sin datos",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }

        System.out.println("[DashboardView] ====== ACTUALIZACIÓN COMPLETADA ======\n");
    }
}