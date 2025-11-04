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
                e.printStackTrace();
            }
        }
        return List.of();
    }

    public MedicamentoDto create(String codigo, String nombre, String presentacion) {
        // Crear DTO con los nombres correctos de campos
        MedicamentoDto dto = new MedicamentoDto(codigo, nombre, presentacion);

        // Serializar a JSON
        String jsonData = gson.toJson(dto);
        System.out.println("[MedicamentoService] Enviando JSON: " + jsonData);

        RequestDto req = new RequestDto("Medicamentos", "createMedicamento", jsonData, null);
        ResponseDto res = sendRequest(req);

        if (res != null) {
            System.out.println("[MedicamentoService] Respuesta del servidor: success=" + res.isSuccess() + ", message=" + res.getMessage());
            if (res.isSuccess()) {
                return dto;
            } else {
                System.err.println("[MedicamentoService] Error del servidor: " + res.getMessage());
            }
        } else {
            System.err.println("[MedicamentoService] No se recibió respuesta del servidor");
        }

        return null;
    }

    public boolean delete(String codigo) {
        System.out.println("[MedicamentoService] Eliminando medicamento con código: " + codigo);

        RequestDto req = new RequestDto("Medicamentos", "deleteMedicamento", codigo, null);
        ResponseDto res = sendRequest(req);

        if (res != null) {
            System.out.println("[MedicamentoService] Respuesta delete: success=" + res.isSuccess() + ", message=" + res.getMessage());
            return res.isSuccess();
        }

        System.err.println("[MedicamentoService] No se recibió respuesta del servidor");
        return false;
    }

    public List<MedicamentoDto> searchByName(String nombre) {
        List<MedicamentoDto> todos = getAll();
        return todos.stream()
                .filter(m -> m.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }
}