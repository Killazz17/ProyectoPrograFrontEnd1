package Presentation.Models;

import Domain.Dtos.RecetaDetalladaDto;
import org.jfree.data.general.DefaultPieDataset;

import java.util.*;

public class RecetaPieChartModel extends DefaultPieDataset {

    public void mapData(List<RecetaDetalladaDto> recetas) {
        clear();

        if (recetas == null || recetas.isEmpty()) {
            return;
        }

        // Mapa: Estado -> Cantidad
        Map<String, Integer> cantidadPorEstado = new HashMap<>();

        for (RecetaDetalladaDto receta : recetas) {
            String estado = receta.getEstado() != null ? receta.getEstado() : "SIN_ESTADO";
            cantidadPorEstado.put(estado, cantidadPorEstado.getOrDefault(estado, 0) + 1);
        }

        // Agregar datos al dataset
        for (Map.Entry<String, Integer> entry : cantidadPorEstado.entrySet()) {
            setValue(entry.getKey(), entry.getValue());
        }
    }
}