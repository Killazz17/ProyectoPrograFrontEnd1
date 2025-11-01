package Services;

import Domain.Dtos.MedicoDto;
import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class MedicoService extends BaseService {

    public MedicoService() {
        super();
    }

    public MedicoService(String host, int port) {
        super(host, port);
    }

    public List<MedicoDto> getAll() {
        RequestDto req = new RequestDto("Medico", "getAll", null, null);
        ResponseDto res = sendRequest(req);
        if (res != null && res.isSuccess()) {
            return gson.fromJson(res.getData(), new TypeToken<List<MedicoDto>>() {}.getType());
        }
        return List.of();
    }

    public MedicoDto create(MedicoDto dto) {
        RequestDto req = new RequestDto("Medico", "create", gson.toJson(dto), null);
        ResponseDto res = sendRequest(req);
        return (res != null && res.isSuccess())
                ? gson.fromJson(res.getData(), MedicoDto.class)
                : null;
    }

    public boolean delete(int id) {
        RequestDto req = new RequestDto("Medico", "delete", gson.toJson(id), null);
        ResponseDto res = sendRequest(req);
        return res != null && res.isSuccess();
    }

    public List<MedicoDto> searchByName(String nombre) {
        RequestDto req = new RequestDto("Medico", "searchByName", gson.toJson(nombre), null);
        ResponseDto res = sendRequest(req);
        if (res != null && res.isSuccess()) {
            return gson.fromJson(res.getData(), new TypeToken<List<MedicoDto>>() {}.getType());
        }
        return List.of();
    }
}