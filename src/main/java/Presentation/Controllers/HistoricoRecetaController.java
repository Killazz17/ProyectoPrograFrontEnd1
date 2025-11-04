package Presentation.Controllers;

import Domain.Dtos.HistoricoRecetaDto;
import Domain.Dtos.RecetaDetalladaDto;
import Presentation.IObserver;
import Presentation.Observable;
import Services.HistoricoRecetaService;
import Utilities.EventType;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HistoricoRecetaController extends Observable {

    private final HistoricoRecetaService service;
    private String nombrePacienteSeleccionado = "";

    public HistoricoRecetaController(HistoricoRecetaService service) {
        this.service = service;
    }

    public void setNombrePacienteSeleccionado(String nombre) {
        this.nombrePacienteSeleccionado = nombre != null ? nombre : "";
    }

    public void listarHistoricoAsync() {
        SwingWorker<List<HistoricoRecetaDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<HistoricoRecetaDto> doInBackground() throws Exception {
                return service.getAll();
            }

            @Override
            protected void done() {
                try {
                    List<HistoricoRecetaDto> recetas = get();
                    System.out.println("[HistoricoController] Recetas cargadas: " + recetas.size());
                    notifyObservers(EventType.UPDATED, recetas);
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al listar histórico", e);
                }
            }
        };
        worker.execute();
    }

    // ÚNICO MÉTODO DE BÚSQUEDA
    public void buscarHistoricoAsync(String tipo, String valor) {
        SwingWorker<List<HistoricoRecetaDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<HistoricoRecetaDto> doInBackground() throws Exception {
                System.out.println("[HistoricoController] Buscando: " + tipo + " = " + valor);

                if ("id_receta".equals(tipo)) {
                    int id = Integer.parseInt(valor);
                    return service.buscarPorIdReceta(id);  // NUEVO MÉTODO
                } else {
                    return service.searchByFilter(tipo, valor);
                }
            }

            @Override
            protected void done() {
                try {
                    List<HistoricoRecetaDto> recetas = get();
                    System.out.println("[HistoricoController] Resultados: " + recetas.size());
                    notifyObservers(EventType.UPDATED, recetas);
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al buscar recetas", e);
                }
            }
        };
        worker.execute();
    }

    public void obtenerDetalleAsync(int idReceta) {
        SwingWorker<RecetaDetalladaDto, Void> worker = new SwingWorker<>() {
            @Override
            protected RecetaDetalladaDto doInBackground() throws Exception {
                return service.getDetalleReceta(idReceta);
            }

            @Override
            protected void done() {
                try {
                    RecetaDetalladaDto detalle = get();
                    if (detalle != null) {
                        detalle.setPacienteNombre(nombrePacienteSeleccionado);
                        notifyObservers(EventType.DETAIL_LOADED, detalle);
                    } else {
                        showError("Receta no encontrada", null);
                    }
                } catch (Exception e) {
                    showError("Error al obtener detalle", e);
                }
            }
        };
        worker.execute();
    }

    private void showError(String msg, Exception e) {
        String error = e != null ? msg + "\n" + e.getMessage() : msg;
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE));
    }

    @Override
    public void addObserver(IObserver observer) { super.addObserver(observer); }
    @Override
    public void notifyObservers(EventType eventType, Object data) { super.notifyObservers(eventType, data); }
}