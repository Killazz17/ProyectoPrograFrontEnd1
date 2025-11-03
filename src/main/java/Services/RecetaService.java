// src/main/java/Services/RecetaService.java
package Services;

import Domain.Dtos.RecetaDetalladaDto;
import Domain.Dtos.RecetaDto;
import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class RecetaService extends BaseService {

    public RecetaService() {
        super();
    }

    public RecetaService(String host, int port) {
        super(host, port);
    }

    /**
     * Obtener todas las recetas para el dashboard
     */
    public List<RecetaDto> getAll() {
        RequestDto req = new RequestDto("Recetas", "getAllRecetas", null, null);
        ResponseDto res = sendRequest(req);
        if (res != null && res.isSuccess() && res.getData() != null) {
            try {
                return gson.fromJson(res.getData(), new TypeToken<List<RecetaDto>>() {}.getType());
            } catch (Exception e) {
                System.err.println("[RecetaService] Error al parsear lista: " + e.getMessage());
            }
        }
        return List.of();
    }

    // Agregar al RecetaService.java existente
    public List<RecetaDetalladaDto> getAllDetalladas() {
        RequestDto req = new RequestDto("Recetas", "getAllRecetasDetalladas", null, null);
        ResponseDto res = sendRequest(req);
        if (res != null && res.isSuccess() && res.getData() != null) {
            try {
                return gson.fromJson(res.getData(), new TypeToken<List<RecetaDetalladaDto>>() {}.getType());
            } catch (Exception e) {
                System.err.println("[RecetaService] Error al parsear lista detallada: " + e.getMessage());
            }
        }
        return List.of();
    }
}