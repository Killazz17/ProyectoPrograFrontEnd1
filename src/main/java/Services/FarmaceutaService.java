package Services;

import Domain.Dtos.FarmaceutaDto;
import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class FarmaceutaService extends BaseService {

    public FarmaceutaService() {
        super();
    }

    public FarmaceutaService(String host, int port) {
        super(host, port);
    }

    public List<FarmaceutaDto> getAll() {
        RequestDto req = new RequestDto("Farmaceutas", "getAllFarmaceutas", null, null);
        ResponseDto res = sendRequest(req);
        if (res != null && res.isSuccess() && res.getData() != null) {
            try {
                return gson.fromJson(res.getData(), new TypeToken<List<FarmaceutaDto>>() {}.getType());
            } catch (Exception e) {
                System.err.println("[FarmaceutaService] Error al parsear lista: " + e.getMessage());
            }
        }
        return List.of();
    }

    public FarmaceutaDto create(int id, String nombre) {
        FarmaceutaDto dto = new FarmaceutaDto(id, nombre);
        RequestDto req = new RequestDto("Farmaceutas", "createFarmaceuta", gson.toJson(dto), null);
        ResponseDto res = sendRequest(req);
        return (res != null && res.isSuccess()) ? dto : null;
    }

    public boolean delete(int id) {
        RequestDto req = new RequestDto("Farmaceutas", "deleteFarmaceuta", gson.toJson(id), null);
        ResponseDto res = sendRequest(req);
        return res != null && res.isSuccess();
    }

    public List<FarmaceutaDto> searchByName(String nombre) {
        List<FarmaceutaDto> todos = getAll();
        return todos.stream()
                .filter(f -> f.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }
}