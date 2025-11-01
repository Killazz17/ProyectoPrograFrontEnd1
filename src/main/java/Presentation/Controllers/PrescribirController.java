package Presentation.Controllers;

import Domain.Dtos.RecetaDto;
import Presentation.Observable;
import Services.PrescripcionService;
import Utilities.EventType;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PrescribirController extends Observable {
    private final PrescripcionService service;

    public PrescribirController(PrescripcionService service) {
        this.service = service;
    }

    // === Listar recetas ===
    public void listarRecetasAsync() {
        SwingWorker<List<RecetaDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<RecetaDto> doInBackground() throws Exception {
                return service.getAll();
            }

            @Override
            protected void done() {
                try {
                    notifyObservers(EventType.UPDATED, get());
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al listar recetas", e);
                }
            }
        };
        worker.execute();
    }

    // === Crear receta ===
    public void crearRecetaAsync(RecetaDto dto) {
        SwingWorker<RecetaDto, Void> worker = new SwingWorker<>() {
            @Override
            protected RecetaDto doInBackground() throws Exception {
                return service.create(dto);
            }

            @Override
            protected void done() {
                try {
                    get();
                    notifyObservers(EventType.CREATED, dto);
                    listarRecetasAsync();
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al crear receta", e);
                }
            }
        };
        worker.execute();
    }

    // === Eliminar receta ===
    public void eliminarRecetaAsync(int id) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return service.delete(id);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        notifyObservers(EventType.DELETED, id);
                        listarRecetasAsync();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al eliminar receta", e);
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