package Services;

import Domain.Dtos.MedicamentoDto;
import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class MedicamentoService extends BaseService {

    public MedicamentoService() {
        super();
    }

    public MedicamentoService(String host, int port) {
        super(host, port);
    }

    public List<MedicamentoDto> getAll() {
        RequestDto req = new RequestDto("Medicamentos", "getAllMedicamentos", null, null);
        ResponseDto res = sendRequest(req);
        if (res != null && res.isSuccess() && res.getData() != null) {
            try {
                return gson.fromJson(res.getData(), new TypeToken<List<MedicamentoDto>>() {}.getType());
            } catch (Exception e) {
                System.err.println("[MedicamentoService] Error al parsear lista: " + e.getMessage());
            }
        }
        return List.of();
    }

    public MedicamentoDto create(String codigo, String nombre, String descripcion) {
        MedicamentoDto dto = new MedicamentoDto(codigo, nombre, descripcion);
        RequestDto req = new RequestDto("Medicamentos", "createMedicamento", gson.toJson(dto), null);
        ResponseDto res = sendRequest(req);
        return (res != null && res.isSuccess()) ? dto : null;
    }

    public boolean delete(String codigo) {
        RequestDto req = new RequestDto("Medicamentos", "deleteMedicamento", gson.toJson(codigo), null);
        ResponseDto res = sendRequest(req);
        return res != null && res.isSuccess();
    }

    public List<MedicamentoDto> searchByName(String nombre) {
        List<MedicamentoDto> todos = getAll();
        return todos.stream()
                .filter(m -> m.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }
}