// src/main/java/Presentation/Controllers/DashboardController.java
package Presentation.Controllers;

import Domain.Dtos.RecetaDetalladaDto;
import Presentation.Observable;
import Services.RecetaService;
import Utilities.EventType;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DashboardController extends Observable {
    private final RecetaService service;

    public DashboardController(RecetaService service) {
        this.service = service;
    }

    public void cargarDatosAsync() {
        SwingWorker<List<RecetaDetalladaDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<RecetaDetalladaDto> doInBackground() throws Exception {
                return service.getAllDetalladas();
            }

            @Override
            protected void done() {
                try {
                    notifyObservers(EventType.UPDATED, get());
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al cargar datos del dashboard", e);
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