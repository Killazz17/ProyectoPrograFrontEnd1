package Services;

import Domain.Dtos.RecetaDto;
import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class PrescripcionService extends BaseService {

    public PrescripcionService(String host, int port) {
        super(host, port);
    }

    public List<RecetaDto> getAll() {
        RequestDto req = new RequestDto("Receta", "getAll", null, null);
        ResponseDto res = sendRequest(req);
        if (res != null && res.isSuccess()) {
            return gson.fromJson(res.getData(), new TypeToken<List<RecetaDto>>() {}.getType());
        }
        return List.of();
    }

    public RecetaDto create(RecetaDto dto) {
        RequestDto req = new RequestDto("Receta", "create", gson.toJson(dto), null);
        ResponseDto res = sendRequest(req);
        return (res != null && res.isSuccess())
                ? gson.fromJson(res.getData(), RecetaDto.class)
                : null;
    }

    public boolean delete(int id) {
        RequestDto req = new RequestDto("Receta", "delete", gson.toJson(id), null);
        ResponseDto res = sendRequest(req);
        return res != null && res.isSuccess();
    }
}