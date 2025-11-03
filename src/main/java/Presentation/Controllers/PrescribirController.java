package Presentation.Controllers;

import Domain.Dtos.RecetaCreateDto;
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

    // === Crear receta usando RecetaCreateDto ===
    public void crearRecetaAsync(RecetaCreateDto dto) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                System.out.println("[PrescribirController] Creando receta: " + dto);
                return service.create(dto);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        System.out.println("[PrescribirController] Receta creada exitosamente");
                        notifyObservers(EventType.CREATED, dto);
                        listarRecetasAsync();
                    } else {
                        System.err.println("[PrescribirController] Falló la creación de receta");
                        showError("Error al crear receta", new Exception("El servidor rechazó la receta"));
                    }
                } catch (InterruptedException | ExecutionException e) {
                    System.err.println("[PrescribirController] Excepción al crear receta: " + e.getMessage());
                    e.printStackTrace();
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
        String errorMsg = msg + "\n" + e.getMessage();
        if (e.getCause() != null) {
            errorMsg += "\nCausa: " + e.getCause().getMessage();
        }
        JOptionPane.showMessageDialog(null, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}