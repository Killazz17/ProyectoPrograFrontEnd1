// src/main/java/Presentation/Models/RecetaLineChartModel.java
package Presentation.Models;

import Domain.Dtos.MedicamentoPrescritoDto;
import Domain.Dtos.RecetaDetalladaDto;
import org.jfree.data.category.DefaultCategoryDataset;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RecetaLineChartModel extends DefaultCategoryDataset {

    public void mapData(List<RecetaDetalladaDto> recetas, String medicamentoFiltro) {
        clear();

        if (recetas == null || recetas.isEmpty()) {
            System.out.println("[RecetaLineChartModel] No hay recetas para procesar");
            return;
        }

        System.out.println("[RecetaLineChartModel] ====== PROCESANDO DATOS ======");
        System.out.println("[RecetaLineChartModel] Recetas: " + recetas.size());
        System.out.println("[RecetaLineChartModel] Medicamento filtro: " + medicamentoFiltro);

        // Mapa: Mes -> Cantidad
        Map<String, Integer> cantidadPorMes = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (RecetaDetalladaDto receta : recetas) {
            // ✅ FIX: Usar fechaConfeccion en lugar de getFecha()
            if (receta.getFechaConfeccion() == null || receta.getMedicamentos() == null) {
                System.out.println("[RecetaLineChartModel] Receta " + receta.getId() +
                        " sin fecha o medicamentos");
                continue;
            }

            try {
                LocalDate fecha = LocalDate.parse(receta.getFechaConfeccion(), formatter);
                String mes = String.format("%d-%02d", fecha.getYear(), fecha.getMonthValue());

                System.out.println("[RecetaLineChartModel] Receta " + receta.getId() +
                        " - Fecha: " + fecha + " - Mes: " + mes);

                // Contar medicamentos que coincidan con el filtro
                for (MedicamentoPrescritoDto med : receta.getMedicamentos()) {
                    System.out.println("[RecetaLineChartModel]   Med: " + med.getNombre() +
                            " vs Filtro: " + medicamentoFiltro);

                    if (med.getNombre() != null &&
                            med.getNombre().equalsIgnoreCase(medicamentoFiltro)) {

                        int cantidadActual = cantidadPorMes.getOrDefault(mes, 0);
                        int nuevaCantidad = cantidadActual + med.getCantidad();
                        cantidadPorMes.put(mes, nuevaCantidad);

                        System.out.println("[RecetaLineChartModel]   ✓ Match! Cantidad: " +
                                med.getCantidad() + " (Total mes: " + nuevaCantidad + ")");
                    }
                }
            } catch (Exception e) {
                System.err.println("[RecetaLineChartModel] Error al procesar receta " +
                        receta.getId() + ": " + e.getMessage());
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
                System.out.println("[RecetaLineChartModel] Agregado: " +
                        entry.getKey() + " = " + entry.getValue());
            }
        }

        System.out.println("[RecetaLineChartModel] ====== FIN PROCESAMIENTO ======");
    }
}