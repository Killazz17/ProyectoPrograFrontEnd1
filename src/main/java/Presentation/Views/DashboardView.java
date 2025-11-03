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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private JButton refreshButton;

    private List<MedicamentoPrescritoDetalladoDto> medicamentosActuales;
    private final DashboardController controller;

    public DashboardView() {
        MedicamentoPrescritoService service = new MedicamentoPrescritoService();
        controller = new DashboardController(service);
        controller.addObserver(this);

        medicamentosActuales = new ArrayList<>();
        MainPanel = new JPanel(new BorderLayout());

        lineModel = new RecetaLineChartModel();
        pieModel = new RecetaPieChartModel();

        crearGraficos();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                lineChartPanel, pieChartPanel);
        splitPane.setDividerLocation(650);
        splitPane.setResizeWeight(0.6);
        MainPanel.add(splitPane, BorderLayout.CENTER);

        crearPanelControles();

        setLayout(new BorderLayout());
        add(MainPanel, BorderLayout.CENTER);

        controller.cargarDatosAsync();
    }

    private void crearGraficos() {
        lineChart = ChartFactory.createLineChart(
                "Medicamentos Prescritos por Mes",
                "Mes",
                "Cantidad",
                lineModel,
                PlotOrientation.VERTICAL,
                true,  // leyenda
                true,  // tooltips
                false  // urls
        );

        CategoryPlot plot = (CategoryPlot) lineChart.getPlot();
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setDefaultShapesVisible(true);
        renderer.setDefaultShapesFilled(true);
        plot.setRenderer(renderer);

        lineChartPanel = new ChartPanel(lineChart);
        lineChartPanel.setPreferredSize(new Dimension(800, 600));

        pieChart = ChartFactory.createPieChart(
                "Recetas por Estado",
                pieModel,
                true,
                true,
                false
        );

        pieChartPanel = new ChartPanel(pieChart);
        pieChartPanel.setPreferredSize(new Dimension(400, 600));
    }

    private void crearPanelControles() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        medicamentoCombo = new JComboBox<>();
        medicamentoCombo.addItem("Seleccione medicamento");
        medicamentoCombo.setPreferredSize(new Dimension(250, 30));

        int anioActual = LocalDate.now().getYear();
        Integer[] anios = new Integer[10];
        for (int i = 0; i < 10; i++) {
            anios[i] = anioActual - 5 + i;
        }

        desdeAnio = new JComboBox<>(anios);
        hastaAnio = new JComboBox<>(anios);
        desdeAnio.setSelectedItem(anioActual);
        hastaAnio.setSelectedItem(anioActual);

        String[] meses = {
                "01-Ene", "02-Feb", "03-Mar", "04-Abr", "05-May", "06-Jun",
                "07-Jul", "08-Ago", "09-Sep", "10-Oct", "11-Nov", "12-Dic"
        };

        desdeMes = new JComboBox<>(meses);
        hastaMes = new JComboBox<>(meses);
        desdeMes.setSelectedIndex(0);
        hastaMes.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        filtrarButton = new JButton("Filtrar");
        filtrarButton.setBackground(new Color(76, 175, 80));
        filtrarButton.setForeground(Color.WHITE);
        filtrarButton.setFocusPainted(false);
        filtrarButton.setOpaque(true);
        filtrarButton.setBorderPainted(false);
        filtrarButton.addActionListener(e -> aplicarFiltros());

        refreshButton = new JButton("Recargar");
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> recargarDatos());

        desdeAnio.addActionListener(e -> validarRangoFechas());
        desdeMes.addActionListener(e -> validarRangoFechas());
        hastaAnio.addActionListener(e -> validarRangoFechas());
        hastaMes.addActionListener(e -> validarRangoFechas());

        topPanel.add(new JLabel("Desde:"));
        topPanel.add(desdeAnio);
        topPanel.add(desdeMes);

        topPanel.add(Box.createHorizontalStrut(10));

        topPanel.add(new JLabel("Hasta:"));
        topPanel.add(hastaAnio);
        topPanel.add(hastaMes);

        topPanel.add(Box.createHorizontalStrut(20));

        topPanel.add(new JLabel("Medicamento:"));
        topPanel.add(medicamentoCombo);

        topPanel.add(Box.createHorizontalStrut(10));

        topPanel.add(filtrarButton);
        topPanel.add(refreshButton);

        MainPanel.add(topPanel, BorderLayout.NORTH);
    }

    private void validarRangoFechas() {
        try {
            int anioInicio = (Integer) desdeAnio.getSelectedItem();
            int mesInicio = desdeMes.getSelectedIndex() + 1;
            int anioFin = (Integer) hastaAnio.getSelectedItem();
            int mesFin = hastaMes.getSelectedIndex() + 1;

            LocalDate inicio = LocalDate.of(anioInicio, mesInicio, 1);
            LocalDate fin = LocalDate.of(anioFin, mesFin, 1);

            if (inicio.isAfter(fin)) {
                filtrarButton.setEnabled(false);
                filtrarButton.setToolTipText("La fecha 'Desde' no puede ser posterior a 'Hasta'");
                filtrarButton.setBackground(Color.GRAY);
            } else {
                filtrarButton.setEnabled(true);
                filtrarButton.setToolTipText("Aplicar filtros");
                filtrarButton.setBackground(new Color(76, 175, 80));
            }
        } catch (Exception e) {
            filtrarButton.setEnabled(false);
        }
    }

    private void aplicarFiltros() {
        String medicamentoSeleccionado = (String) medicamentoCombo.getSelectedItem();

        if (medicamentoSeleccionado == null || medicamentoSeleccionado.startsWith("--")) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un medicamento",
                    "Seleccion requerida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int anioInicio = (Integer) desdeAnio.getSelectedItem();
        int mesInicio = desdeMes.getSelectedIndex() + 1;
        int anioFin = (Integer) hastaAnio.getSelectedItem();
        int mesFin = hastaMes.getSelectedIndex() + 1;

        LocalDate inicio = LocalDate.of(anioInicio, mesInicio, 1);
        LocalDate fin = LocalDate.of(anioFin, mesFin, 1).plusMonths(1).minusDays(1);

        if (inicio.isAfter(fin)) {
            JOptionPane.showMessageDialog(this,
                    "La fecha 'Desde' no puede ser posterior a 'Hasta'.\n" +
                            "Por favor, ajuste las fechas correctamente.",
                    "Error en rango de fechas",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<MedicamentoPrescritoDetalladoDto> filtrados = new ArrayList<>();

        for (MedicamentoPrescritoDetalladoDto mp : medicamentosActuales) {
            if (mp.getFechaConfeccion() == null || mp.getMedicamentoNombre() == null) {
                continue;
            }

            if (!mp.getMedicamentoNombre().trim().equalsIgnoreCase(medicamentoSeleccionado.trim())) {
                continue;
            }

            try {
                LocalDate fecha = LocalDate.parse(mp.getFechaConfeccion().trim(), formatter);

                if (!fecha.isBefore(inicio) && !fecha.isAfter(fin)) {
                    filtrados.add(mp);
                }
            } catch (Exception e) {
                System.err.println("[DashboardView] Error parseando fecha '" +
                        mp.getFechaConfeccion() + "': " + e.getMessage());
            }
        }

        if (filtrados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay datos para el medicamento '" + medicamentoSeleccionado +
                            "'\nen el rango de fechas seleccionado.",
                    "Sin datos",
                    JOptionPane.INFORMATION_MESSAGE);

            lineModel.clear();
            lineChart.fireChartChanged();
            lineChartPanel.repaint();
        } else {
            lineModel.mapData(filtrados, medicamentoSeleccionado);

            lineChart.fireChartChanged();
            lineChartPanel.validate();
            lineChartPanel.repaint();
        }

    }

    private void recargarDatos() {

        medicamentosActuales.clear();
        medicamentoCombo.removeAllItems();
        medicamentoCombo.addItem("Seleccione medicamento");

        lineModel.clear();
        lineChart.fireChartChanged();

        pieModel.clear();
        pieChart.fireChartChanged();

        controller.cargarDatosAsync();
    }

    @Override
    public void update(EventType eventType, Object data) {
        if (eventType == EventType.UPDATED && data instanceof List) {
            SwingUtilities.invokeLater(() -> {
                actualizarDatos((List<MedicamentoPrescritoDetalladoDto>) data);
            });
        }
    }

    private void actualizarDatos(List<MedicamentoPrescritoDetalladoDto> medicamentos) {
        if (medicamentos == null || medicamentos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se encontraron medicamentos prescritos.\n" +
                            "Verifique que existan recetas en la base de datos.",
                    "Sin datos",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        this.medicamentosActuales = new ArrayList<>(medicamentos);

        Set<String> nombresMedicamentos = new TreeSet<>();

        for (MedicamentoPrescritoDetalladoDto mp : medicamentos) {
            if (mp.getMedicamentoNombre() != null && !mp.getMedicamentoNombre().trim().isEmpty()) {
                String nombre = mp.getMedicamentoNombre().trim();
                nombresMedicamentos.add(nombre);
            }
        }

        medicamentoCombo.removeAllItems();
        medicamentoCombo.addItem(" Seleccione medicamento ");

        for (String nombre : nombresMedicamentos) {
            medicamentoCombo.addItem(nombre);
        }

        Map<String, Integer> estadoCounts = new HashMap<>();
        for (MedicamentoPrescritoDetalladoDto mp : medicamentos) {
            String estado = mp.getEstado() != null ? mp.getEstado().toUpperCase() : "SIN ESTADO";
            estadoCounts.put(estado, estadoCounts.getOrDefault(estado, 0) + 1);
        }

        pieModel.clear();
        for (Map.Entry<String, Integer> entry : estadoCounts.entrySet()) {
            pieModel.setValue(entry.getKey(), entry.getValue());
        }
        pieChart.fireChartChanged();
        pieChartPanel.repaint();
    }
}