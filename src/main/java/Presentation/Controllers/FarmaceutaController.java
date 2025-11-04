package Presentation.Controllers;

import Domain.Dtos.FarmaceutaDto;
import Presentation.Observable;
import Services.FarmaceutaService;
import Utilities.EventType;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FarmaceutaController extends Observable {
    private final FarmaceutaService service;

    public FarmaceutaController(FarmaceutaService service) {
        this.service = service;
    }

    public void listarFarmaceutasAsync() {
        SwingWorker<List<FarmaceutaDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<FarmaceutaDto> doInBackground() throws Exception {
                return service.getAll();
            }

            @Override
            protected void done() {
                try {
                    notifyObservers(EventType.UPDATED, get());
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al listar farmaceutas", e);
                }
            }
        };
        worker.execute();
    }

    public void crearFarmaceutaAsync(FarmaceutaDto dto) {
        SwingWorker<FarmaceutaDto, Void> worker = new SwingWorker<>() {
            @Override
            protected FarmaceutaDto doInBackground() throws Exception {
                return service.create(dto.getId(), dto.getNombre());
            }

            @Override
            protected void done() {
                try {
                    get();
                    notifyObservers(EventType.CREATED, dto);
                    listarFarmaceutasAsync();
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al crear farmaceuta", e);
                }
            }
        };
        worker.execute();
    }

    public void eliminarFarmaceutaAsync(int id) {
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
                        listarFarmaceutasAsync();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al eliminar farmaceuta", e);
                }
            }
        };
        worker.execute();
    }

    public void buscarFarmaceutaAsync(String nombre) {
        SwingWorker<List<FarmaceutaDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<FarmaceutaDto> doInBackground() throws Exception {
                return service.searchByName(nombre);
            }

            @Override
            protected void done() {
                try {
                    notifyObservers(EventType.UPDATED, get());
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al buscar farmaceuta", e);
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