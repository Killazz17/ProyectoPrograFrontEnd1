package Presentation.Models;

import Domain.Dtos.MedicamentoPrescritoDetalladoDto;
import org.jfree.data.category.DefaultCategoryDataset;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Modelo de gráfico de líneas que trabaja directamente con medicamentos prescritos
 */
public class RecetaLineChartModel extends DefaultCategoryDataset {

    public void mapData(List<MedicamentoPrescritoDetalladoDto> medicamentosPrescritos,
                        String medicamentoFiltro) {
        clear();

        if (medicamentosPrescritos == null || medicamentosPrescritos.isEmpty()) {
            System.out.println("[RecetaLineChartModel] No hay medicamentos prescritos para procesar");
            return;
        }

        System.out.println("[RecetaLineChartModel] ====== PROCESANDO DATOS ======");
        System.out.println("[RecetaLineChartModel] Medicamentos prescritos: "
                + medicamentosPrescritos.size());
        System.out.println("[RecetaLineChartModel] Medicamento filtro: " + medicamentoFiltro);

        // Mapa: Mes -> Cantidad
        Map<String, Integer> cantidadPorMes = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (MedicamentoPrescritoDetalladoDto mp : medicamentosPrescritos) {
            if (mp.getFechaConfeccion() == null || mp.getMedicamentoNombre() == null) {
                System.out.println("[RecetaLineChartModel] Medicamento prescrito sin fecha o nombre");
                continue;
            }

            // Verificar si coincide con el filtro
            if (!mp.getMedicamentoNombre().equalsIgnoreCase(medicamentoFiltro)) {
                continue;
            }

            try {
                LocalDate fecha = LocalDate.parse(mp.getFechaConfeccion(), formatter);
                String mes = String.format("%d-%02d", fecha.getYear(), fecha.getMonthValue());

                System.out.println("[RecetaLineChartModel] Procesando: "
                        + mp.getMedicamentoNombre() + " - Fecha: " + fecha + " - Mes: " + mes);

                int cantidadActual = cantidadPorMes.getOrDefault(mes, 0);
                int nuevaCantidad = cantidadActual + mp.getCantidad();
                cantidadPorMes.put(mes, nuevaCantidad);

                System.out.println("[RecetaLineChartModel]   ✓ Cantidad: "
                        + mp.getCantidad() + " (Total mes: " + nuevaCantidad + ")");

            } catch (Exception e) {
                System.err.println("[RecetaLineChartModel] Error al procesar medicamento prescrito "
                        + mp.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("[RecetaLineChartModel] Datos por mes: " + cantidadPorMes);

        // Agregar datos al dataset
        if (cantidadPorMes.isEmpty()) {
            System.out.println("[RecetaLineChartModel] ⚠️ No hay datos para mostrar");
        } else {
            for (Map.Entry<String, Integer> entry : cantidadPorMes.entrySet()) {
                addValue(entry.getValue(), medicamentoFiltro, entry.getKey());
                System.out.println("[RecetaLineChartModel] Agregado: "
                        + entry.getKey() + " = " + entry.getValue());
            }
        }

        System.out.println("[RecetaLineChartModel] ====== FIN PROCESAMIENTO ======");
    }
}