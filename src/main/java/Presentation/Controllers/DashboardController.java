package Presentation.Controllers;

import Domain.Dtos.MedicamentoPrescritoDetalladoDto;
import Presentation.Observable;
import Services.MedicamentoPrescritoService;
import Utilities.EventType;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DashboardController extends Observable {
    private final MedicamentoPrescritoService service;

    public DashboardController(MedicamentoPrescritoService service) {
        this.service = service;
    }

    public void cargarDatosAsync() {
        SwingWorker<List<MedicamentoPrescritoDetalladoDto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<MedicamentoPrescritoDetalladoDto> doInBackground() throws Exception {
                return service.getAllDetallados();
            }

            @Override
            protected void done() {
                try {
                    List<MedicamentoPrescritoDetalladoDto> datos = get();
                    System.out.println("[DashboardController] Datos cargados: " + datos.size());
                    notifyObservers(EventType.UPDATED, datos);
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error al cargar datos del dashboard", e);
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