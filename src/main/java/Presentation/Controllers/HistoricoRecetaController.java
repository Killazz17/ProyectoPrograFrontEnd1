package Presentation.Controllers;

import Domain.Dtos.HistoricoRecetaDto;
import Presentation.Observable;
import Services.HistoricoRecetaService;
import Utilities.EventType;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HistoricoRecetaController extends Observable {

    private final HistoricoRecetaService service;

    public HistoricoRecetaController(HistoricoRecetaService service) {
        this.service = service;
    }

    // === Listar recetas del paciente ===
    public void listarHistoricoAsync() {
        SwingWorker<List<HistoricoRecetaDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<HistoricoRecetaDto> doInBackground() throws Exception {
                return service.getAll();
            }

            @Override
            protected void done() {
                try {
                    notifyObservers(EventType.UPDATED, get());
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al listar histórico", e);
                }
            }
        };
        worker.execute();
    }

    // === Buscar receta filtrando por texto ===
    public void buscarHistoricoAsync(String filtro) {
        SwingWorker<List<HistoricoRecetaDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<HistoricoRecetaDto> doInBackground() throws Exception {
                return service.searchByFilter(filtro);
            }

            @Override
            protected void done() {
                try {
                    notifyObservers(EventType.UPDATED, get());
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al buscar recetas", e);
                }
            }
        };
        worker.execute();
    }

    private void showError(String msg, Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, msg + "\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}