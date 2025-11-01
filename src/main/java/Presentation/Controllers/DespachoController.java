package Presentation.Controllers;

import Domain.Dtos.DespachoDto;
import Presentation.Observable;
import Services.DespachoService;
import Utilities.EventType;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DespachoController extends Observable {

    private final DespachoService service;

    public DespachoController(DespachoService service) {
        this.service = service;
    }

    // === Listar todos los despachos ===
    public void listarDespachosAsync() {
        SwingWorker<List<DespachoDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<DespachoDto> doInBackground() throws Exception {
                return service.getAll();
            }

            @Override
            protected void done() {
                try {
                    notifyObservers(EventType.UPDATED, get());
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al listar despachos", e);
                }
            }
        };
        worker.execute();
    }

    // === Cambiar estado del despacho ===
    public void actualizarEstadoAsync(int id, String nuevoEstado) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return service.updateState(id, nuevoEstado);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        notifyObservers(EventType.UPDATED, null);
                        listarDespachosAsync();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al actualizar estado del despacho", e);
                }
            }
        };
        worker.execute();
    }

    // === Buscar despacho por ID ===
    public void buscarDespachoPorIdAsync(int id) {
        SwingWorker<DespachoDto, Void> worker = new SwingWorker<>() {
            @Override
            protected DespachoDto doInBackground() throws Exception {
                return service.getById(id);
            }

            @Override
            protected void done() {
                try {
                    DespachoDto result = get();
                    notifyObservers(EventType.UPDATED, List.of(result));
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al buscar despacho", e);
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