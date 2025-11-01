package Services;

import Domain.Dtos.MedicamentoDto;
import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class MedicamentoService extends BaseService {

    public MedicamentoService(String host, int port) {
        super(host, port);
    }

    public List<MedicamentoDto> getAll() {
        RequestDto req = new RequestDto("Medicamento", "getAll", null, null);
        ResponseDto res = sendRequest(req);
        if (res != null && res.isSuccess()) {
            return gson.fromJson(res.getData(), new TypeToken<List<MedicamentoDto>>() {}.getType());
        }
        return List.of();
    }

    public MedicamentoDto create(MedicamentoDto dto) {
        RequestDto req = new RequestDto("Medicamento", "create", gson.toJson(dto), null);
        ResponseDto res = sendRequest(req);
        return (res != null && res.isSuccess())
                ? gson.fromJson(res.getData(), MedicamentoDto.class)
                : null;
    }

    public boolean delete(String codigo) {
        RequestDto req = new RequestDto("Medicamento", "delete", gson.toJson(codigo), null);
        ResponseDto res = sendRequest(req);
        return res != null && res.isSuccess();
    }

    public List<MedicamentoDto> searchByName(String nombre) {
        RequestDto req = new RequestDto("Medicamento", "searchByName", gson.toJson(nombre), null);
        ResponseDto res = sendRequest(req);
        if (res != null && res.isSuccess()) {
            return gson.fromJson(res.getData(), new TypeToken<List<MedicamentoDto>>() {}.getType());
        }
        return List.of();
    }
}