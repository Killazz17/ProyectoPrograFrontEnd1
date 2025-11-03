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
    private Integer pacienteIdFiltro = null; // null = todas las recetas

    public DespachoController(DespachoService service) {
        this.service = service;
    }

    /**
     * Establecer filtro por paciente (para cuando el usuario es paciente)
     */
    public void setPacienteFilter(int pacienteId) {
        this.pacienteIdFiltro = pacienteId;
    }

    /**
     * Limpiar filtro de paciente
     */
    public void clearPacienteFilter() {
        this.pacienteIdFiltro = null;
    }

    // === Listar todos los despachos (o filtrados por paciente) ===
    public void listarDespachosAsync() {
        SwingWorker<List<DespachoDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<DespachoDto> doInBackground() throws Exception {
                if (pacienteIdFiltro != null) {
                    return service.getByPaciente(pacienteIdFiltro);
                } else {
                    return service.getAll();
                }
            }

            @Override
            protected void done() {
                try {
                    List<DespachoDto> despachos = get();
                    System.out.println("[DespachoController] Despachos cargados: " + despachos.size());
                    notifyObservers(EventType.UPDATED, despachos);
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
                        JOptionPane.showMessageDialog(null,
                                "Estado actualizado correctamente",
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                        notifyObservers(EventType.UPDATED, null);
                        listarDespachosAsync();
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "No se pudo actualizar el estado",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
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
                    if (result != null) {
                        notifyObservers(EventType.UPDATED, List.of(result));
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "No se encontró la receta con ID: " + id,
                                "No encontrado",
                                JOptionPane.WARNING_MESSAGE);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al buscar despacho", e);
                }
            }
        };
        worker.execute();
    }

    private void showError(String msg, Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, msg + "\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}