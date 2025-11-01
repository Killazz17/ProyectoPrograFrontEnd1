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
        RequestDto req = new RequestDto("Medicos", "getAllMedicos", null, null);
        ResponseDto res = sendRequest(req);
        if (res != null && res.isSuccess() && res.getData() != null) {
            try {
                return gson.fromJson(res.getData(), new TypeToken<List<MedicoDto>>() {}.getType());
            } catch (Exception e) {
                System.err.println("[MedicoService] Error al parsear lista: " + e.getMessage());
            }
        }
        return List.of();
    }

    public MedicoDto create(int id, String nombre, String especialidad) {
        MedicoDto dto = new MedicoDto(id, nombre, especialidad);
        RequestDto req = new RequestDto("Medicos", "createMedico", gson.toJson(dto), null);
        ResponseDto res = sendRequest(req);
        return (res != null && res.isSuccess()) ? dto : null;
    }

    public boolean delete(int id) {
        RequestDto req = new RequestDto("Medicos", "deleteMedico", gson.toJson(id), null);
        ResponseDto res = sendRequest(req);
        return res != null && res.isSuccess();
    }

    public List<MedicoDto> searchByName(String nombre) {
        List<MedicoDto> todos = getAll();
        return todos.stream()
                .filter(m -> m.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }
}