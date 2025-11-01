package Presentation.Controllers;

import Domain.Dtos.PacienteDto;
import Presentation.Observable;
import Services.PacienteService;
import Utilities.EventType;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PacienteController extends Observable {

    private final PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    // === Listar pacientes ===
    public void listarPacientesAsync() {
        SwingWorker<List<PacienteDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<PacienteDto> doInBackground() throws Exception {
                return service.getAll();
            }

            @Override
            protected void done() {
                try {
                    List<PacienteDto> pacientes = get();
                    notifyObservers(EventType.UPDATED, pacientes);
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al listar pacientes", e);
                }
            }
        };
        worker.execute();
    }

    // === Crear paciente ===
    public void crearPacienteAsync(PacienteDto dto) {
        SwingWorker<PacienteDto, Void> worker = new SwingWorker<>() {
            @Override
            protected PacienteDto doInBackground() throws Exception {
                return service.create(dto);
            }

            @Override
            protected void done() {
                try {
                    get();
                    notifyObservers(EventType.CREATED, dto);
                    listarPacientesAsync();
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al crear paciente", e);
                }
            }
        };
        worker.execute();
    }

    // === Eliminar paciente ===
    public void eliminarPacienteAsync(int id) {
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
                        listarPacientesAsync();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al eliminar paciente", e);
                }
            }
        };
        worker.execute();
    }

    // === Buscar paciente por nombre ===
    public void buscarPacienteAsync(String nombre) {
        SwingWorker<List<PacienteDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<PacienteDto> doInBackground() throws Exception {
                return service.searchByName(nombre);
            }

            @Override
            protected void done() {
                try {
                    notifyObservers(EventType.UPDATED, get());
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al buscar paciente", e);
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