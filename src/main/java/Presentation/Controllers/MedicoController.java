package Presentation.Controllers;

import Domain.Dtos.MedicoDto;
import Presentation.Observable;
import Services.MedicoService;
import Utilities.EventType;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MedicoController extends Observable {

    private final MedicoService service;

    public MedicoController(MedicoService service) {
        this.service = service;
    }

    // === Listar médicos ===
    public void listarMedicosAsync() {
        SwingWorker<List<MedicoDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<MedicoDto> doInBackground() throws Exception {
                return service.getAll();
            }

            @Override
            protected void done() {
                try {
                    notifyObservers(EventType.UPDATED, get());
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al listar médicos", e);
                }
            }
        };
        worker.execute();
    }

    // === Crear médico ===
    public void crearMedicoAsync(MedicoDto dto) {
        SwingWorker<MedicoDto, Void> worker = new SwingWorker<>() {
            @Override
            protected MedicoDto doInBackground() throws Exception {
                return service.create(dto);
            }

            @Override
            protected void done() {
                try {
                    get();
                    notifyObservers(EventType.CREATED, dto);
                    listarMedicosAsync();
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al crear médico", e);
                }
            }
        };
        worker.execute();
    }

    // === Eliminar médico ===
    public void eliminarMedicoAsync(int id) {
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
                        listarMedicosAsync();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al eliminar médico", e);
                }
            }
        };
        worker.execute();
    }

    // === Buscar por nombre ===
    public void buscarMedicoAsync(String nombre) {
        SwingWorker<List<MedicoDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<MedicoDto> doInBackground() throws Exception {
                return service.searchByName(nombre);
            }

            @Override
            protected void done() {
                try {
                    notifyObservers(EventType.UPDATED, get());
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al buscar médico", e);
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