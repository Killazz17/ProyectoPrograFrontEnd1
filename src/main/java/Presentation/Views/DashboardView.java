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

        // Filtrar recetas por rango de fechas
        List<RecetaDetalladaDto> filtradas = recetasActuales.stream()
                .filter(r -> {
                    if (r.getFecha() == null) return false;
                    try {
                        LocalDate fecha = LocalDate.parse(r.getFecha());
                        return !fecha.isBefore(inicio) && !fecha.isAfter(fin);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .toList();

        // Actualizar gráfico de línea
        lineModel.mapData(filtradas, medicamentoSeleccionado);
    }

    @Override
    public void update(EventType eventType, Object data) {
        if (eventType == EventType.UPDATED && data instanceof List) {
            actualizarDatos((List<RecetaDetalladaDto>) data);
        }
    }

    private void actualizarDatos(List<RecetaDetalladaDto> recetas) {
        this.recetasActuales = recetas;

        // Extraer nombres únicos de medicamentos
        Set<String> nombresMedicamentos = new TreeSet<>();
        for (RecetaDetalladaDto receta : recetas) {
            if (receta.getMedicamentos() != null) {
                for (MedicamentoPrescritoDto med : receta.getMedicamentos()) {
                    if (med.getNombre() != null && !med.getNombre().isBlank()) {
                        nombresMedicamentos.add(med.getNombre());
                    }
                }
            }
        }

        // Actualizar combo de medicamentos
        medicamentoCombo.removeAllItems();
        medicamentoCombo.addItem("-- Seleccione medicamento --");
        for (String nombre : nombresMedicamentos) {
            medicamentoCombo.addItem(nombre);
        }

        // Actualizar gráfico de pastel (no depende de filtros)
        pieModel.mapData(recetas);

        // Si hay medicamentos, seleccionar el primero y mostrar datos
        if (!nombresMedicamentos.isEmpty()) {
            medicamentoCombo.setSelectedIndex(1); // Primer medicamento
            aplicarFiltros();
        }

        System.out.println("[DashboardView] Datos actualizados: " + recetas.size() + " recetas");
    }
}