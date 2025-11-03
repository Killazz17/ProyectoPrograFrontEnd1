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
        return List.of();
    }

    public boolean create(RecetaCreateDto dto) {
        try {
            String jsonData = gson.toJson(dto);

            RequestDto req = new RequestDto("Recetas", "createReceta", jsonData, null);
            ResponseDto res = sendRequest(req);

            if (res != null) {
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
        return false;
    }
}