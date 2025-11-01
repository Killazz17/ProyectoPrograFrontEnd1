package Services;

import Domain.Dtos.DespachoDto;
import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class DespachoService extends BaseService {

    public DespachoService() {
        super();
    }

    public DespachoService(String host, int port) {
        super(host, port);
    }

    public List<DespachoDto> getAll() {
        RequestDto req = new RequestDto("Despacho", "getAll", null, null);
        ResponseDto res = sendRequest(req);
        if (res != null && res.isSuccess()) {
            return gson.fromJson(res.getData(), new TypeToken<List<DespachoDto>>() {}.getType());
        }
        return List.of();
    }

    public boolean updateState(int id, String nuevoEstado) {
        DespachoDto dto = new DespachoDto(id, 0, nuevoEstado);
        RequestDto req = new RequestDto("Despacho", "updateState", gson.toJson(dto), null);
        ResponseDto res = sendRequest(req);
        return res != null && res.isSuccess();
    }

    public DespachoDto getById(int id) {
        RequestDto req = new RequestDto("Despacho", "getById", gson.toJson(id), null);
        ResponseDto res = sendRequest(req);
        if (res != null && res.isSuccess()) {
            return gson.fromJson(res.getData(), DespachoDto.class);
        }
        return null;
    }
}