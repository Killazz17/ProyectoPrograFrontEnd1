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
            return;
        }

        // Mapa: Mes -> Cantidad
        Map<String, Integer> cantidadPorMes = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (RecetaDetalladaDto receta : recetas) {
            if (receta.getFecha() == null || receta.getMedicamentos() == null) {
                continue;
            }

            try {
                LocalDate fecha = LocalDate.parse(receta.getFecha(), formatter);
                String mes = String.format("%d-%02d", fecha.getYear(), fecha.getMonthValue());

                // Contar medicamentos que coincidan con el filtro
                for (MedicamentoPrescritoDto med : receta.getMedicamentos()) {
                    if (med.getNombre() != null && med.getNombre().equalsIgnoreCase(medicamentoFiltro)) {
                        cantidadPorMes.put(mes, cantidadPorMes.getOrDefault(mes, 0) + med.getCantidad());
                    }
                }
            } catch (Exception e) {
                System.err.println("[RecetaLineChartModel] Error al procesar fecha: " + e.getMessage());
            }
        }

        // Agregar datos al dataset
        for (Map.Entry<String, Integer> entry : cantidadPorMes.entrySet()) {
            addValue(entry.getValue(), medicamentoFiltro, entry.getKey());
        }
    }
}