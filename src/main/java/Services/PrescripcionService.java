package Services;

import Domain.Dtos.RecetaCreateDto;
import Domain.Dtos.RecetaDto;
import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class PrescripcionService extends BaseService {

    public PrescripcionService() {
        super();
    }

    public PrescripcionService(String host, int port) {
        super(host, port);
    }

    public List<RecetaDto> getAll() {
        // El backend no tiene un endpoint para listar todas las recetas aún
        System.out.println("[PrescripcionService] ADVERTENCIA: Backend no tiene endpoint getAll para recetas");
        return List.of();
    }

    /**
     * Crear receta usando RecetaCreateDto (formato del backend)
     */
    public boolean create(RecetaCreateDto dto) {
        try {
            String jsonData = gson.toJson(dto);
            System.out.println("[PrescripcionService] Enviando receta: " + jsonData);

            RequestDto req = new RequestDto("Recetas", "createReceta", jsonData, null);
            ResponseDto res = sendRequest(req);

            if (res != null) {
                System.out.println("[PrescripcionService] Respuesta recibida: success=" + res.isSuccess() + ", message=" + res.getMessage());
                return res.isSuccess();
            }

            System.err.println("[PrescripcionService] No se recibió respuesta del servidor");
            return false;

        } catch (Exception e) {
            System.err.println("[PrescripcionService] Error al crear receta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        System.out.println("[PrescripcionService] ADVERTENCIA: Backend no tiene endpoint delete para recetas");
        return false;
    }
}