package Services;

import Domain.Dtos.PacienteDto;
import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class PacienteService extends BaseService {

    public PacienteService(String host, int port) {
        super(host, port);
    }

    public List<PacienteDto> getAll() {
        RequestDto req = new RequestDto("Paciente", "getAll", null, null);
        ResponseDto res = sendRequest(req);
        if (res != null && res.isSuccess()) {
            return gson.fromJson(res.getData(), new TypeToken<List<PacienteDto>>() {}.getType());
        }
        return List.of();
    }

    public PacienteDto create(PacienteDto dto) {
        String json = gson.toJson(dto);
        RequestDto req = new RequestDto("Paciente", "create", json, null);
        ResponseDto res = sendRequest(req);
        return (res != null && res.isSuccess())
                ? gson.fromJson(res.getData(), PacienteDto.class)
                : null;
    }

    public boolean delete(int id) {
        RequestDto req = new RequestDto("Paciente", "delete", gson.toJson(id), null);
        ResponseDto res = sendRequest(req);
        return res != null && res.isSuccess();
    }

    public List<PacienteDto> searchByName(String nombre) {
        RequestDto req = new RequestDto("Paciente", "searchByName", gson.toJson(nombre), null);
        ResponseDto res = sendRequest(req);
        if (res != null && res.isSuccess()) {
            return gson.fromJson(res.getData(), new TypeToken<List<PacienteDto>>() {}.getType());
        }
        return List.of();
    }
}