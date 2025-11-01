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
        RequestDto req = new RequestDto("Farmaceuta", "getAll", null, null);
        ResponseDto res = sendRequest(req);
        if (res != null && res.isSuccess()) {
            return gson.fromJson(res.getData(), new TypeToken<List<FarmaceutaDto>>() {}.getType());
        }
        return List.of();
    }

    public FarmaceutaDto create(FarmaceutaDto dto) {
        RequestDto req = new RequestDto("Farmaceuta", "create", gson.toJson(dto), null);
        ResponseDto res = sendRequest(req);
        return (res != null && res.isSuccess())
                ? gson.fromJson(res.getData(), FarmaceutaDto.class)
                : null;
    }

    public boolean delete(int id) {
        RequestDto req = new RequestDto("Farmaceuta", "delete", gson.toJson(id), null);
        ResponseDto res = sendRequest(req);
        return res != null && res.isSuccess();
    }

    public List<FarmaceutaDto> searchByName(String nombre) {
        RequestDto req = new RequestDto("Farmaceuta", "searchByName", gson.toJson(nombre), null);
        ResponseDto res = sendRequest(req);
        if (res != null && res.isSuccess()) {
            return gson.fromJson(res.getData(), new TypeToken<List<FarmaceutaDto>>() {}.getType());
        }
        return List.of();
    }
}