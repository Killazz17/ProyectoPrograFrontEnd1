package Presentation.Controllers;

import Domain.Dtos.MedicamentoDto;
import Presentation.Observable;
import Services.MedicamentoService;
import Utilities.EventType;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MedicamentoController extends Observable {

    private final MedicamentoService service;

    public MedicamentoController(MedicamentoService service) {
        this.service = service;
    }

    public void listarMedicamentosAsync() {
        SwingWorker<List<MedicamentoDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<MedicamentoDto> doInBackground() throws Exception {
                return service.getAll();
            }

            @Override
            protected void done() {
                try {
                    notifyObservers(EventType.UPDATED, get());
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al listar medicamentos", e);
                }
            }
        };
        worker.execute();
    }

    public void crearMedicamentoAsync(MedicamentoDto dto) {
        SwingWorker<MedicamentoDto, Void> worker = new SwingWorker<>() {
            @Override
            protected MedicamentoDto doInBackground() throws Exception {
                return service.create(dto.getCodigo(), dto.getNombre(), dto.getDescripcion());
            }

            @Override
            protected void done() {
                try {
                    get();
                    notifyObservers(EventType.CREATED, dto);
                    listarMedicamentosAsync();
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al crear medicamento", e);
                }
            }
        };
        worker.execute();
    }

    public void eliminarMedicamentoAsync(String codigo) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return service.delete(codigo);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        notifyObservers(EventType.DELETED, codigo);
                        listarMedicamentosAsync();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al eliminar medicamento", e);
                }
            }
        };
        worker.execute();
    }

    public void buscarMedicamentoAsync(String nombre) {
        SwingWorker<List<MedicamentoDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<MedicamentoDto> doInBackground() throws Exception {
                return service.searchByName(nombre);
            }

            @Override
            protected void done() {
                try {
                    notifyObservers(EventType.UPDATED, get());
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al buscar medicamento", e);
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