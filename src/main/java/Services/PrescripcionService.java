package Services;

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
        // Por ahora devolvemos lista vacía
        System.out.println("[PrescripcionService] ADVERTENCIA: Backend no tiene endpoint getAll para recetas");
        return List.of();
    }

    public RecetaDto create(RecetaDto dto) {
        RequestDto req = new RequestDto("Recetas", "createReceta", gson.toJson(dto), null);
        ResponseDto res = sendRequest(req);
        return (res != null && res.isSuccess()) ? dto : null;
    }

    public boolean delete(int id) {
        // El backend no tiene endpoint para eliminar recetas
        System.out.println("[PrescripcionService] ADVERTENCIA: Backend no tiene endpoint delete para recetas");
        return false;
    }
}