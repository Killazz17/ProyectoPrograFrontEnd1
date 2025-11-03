package Presentation.Models;

import Domain.Dtos.MedicamentoPrescritoDetalladoDto;
import org.jfree.data.category.DefaultCategoryDataset;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RecetaLineChartModel extends DefaultCategoryDataset {

    public void mapData(List<MedicamentoPrescritoDetalladoDto> medicamentosPrescritos,
                        String medicamentoFiltro) {

        clear();

        if (medicamentosPrescritos == null || medicamentosPrescritos.isEmpty()) {
            return;
        }

        Map<String, Integer> cantidadPorMes = new TreeMap<>();
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        int medicamentosEncontrados = 0;
        int medicamentosIgnorados = 0;

        for (MedicamentoPrescritoDetalladoDto mp : medicamentosPrescritos) {
            if (mp.getFechaConfeccion() == null) {
                medicamentosIgnorados++;
                continue;
            }

            if (mp.getMedicamentoNombre() == null) {
                medicamentosIgnorados++;
                continue;
            }

            if (!mp.getMedicamentoNombre().trim().equalsIgnoreCase(medicamentoFiltro.trim())) {
                continue;
            }

            medicamentosEncontrados++;

            try {
                LocalDate fecha = LocalDate.parse(mp.getFechaConfeccion().trim(), inputFormatter);

                String mes = String.format("%d-%02d", fecha.getYear(), fecha.getMonthValue());

                String mesDisplay = String.format("%s %d",
                        obtenerNombreMes(fecha.getMonthValue()),
                        fecha.getYear()
                );

                int cantidadActual = cantidadPorMes.getOrDefault(mes, 0);
                int nuevaCantidad = cantidadActual + mp.getCantidad();
                cantidadPorMes.put(mes, nuevaCantidad);

            } catch (Exception e) {
                System.err.println("[RecetaLineChartModel] Error al procesar medicamento " +
                        mp.getId() + ": " + e.getMessage());
                e.printStackTrace();
                medicamentosIgnorados++;
            }
        }

        if (cantidadPorMes.isEmpty()) {
            System.out.println("[RecetaLineChartModel] No hay datos para mostrar despues del filtrado");
        } else {
            for (Map.Entry<String, Integer> entry : cantidadPorMes.entrySet()) {
                String mes = entry.getKey();
                Integer cantidad = entry.getValue();

                String[] partes = mes.split("-");
                int anio = Integer.parseInt(partes[0]);
                int mesNum = Integer.parseInt(partes[1]);
                String mesDisplay = String.format("%s %d", obtenerNombreMes(mesNum), anio);

                addValue(cantidad, medicamentoFiltro, mesDisplay);
            }
        }

    }

    private String obtenerNombreMes(int mes) {
        String[] meses = {
                "Ene", "Feb", "Mar", "Abr", "May", "Jun",
                "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
        };
        return meses[mes - 1];
    }
}