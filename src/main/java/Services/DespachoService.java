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
        RequestDto req = new RequestDto("Recetas", "getAllRecetasDespacho", null, null);
        ResponseDto res = sendRequest(req);

        if (res != null && res.isSuccess() && res.getData() != null) {
            try {
                return gson.fromJson(res.getData(), new TypeToken<List<DespachoDto>>() {}.getType());
            } catch (Exception e) {
                System.err.println("[DespachoService] Error al parsear: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return List.of();
    }

    public List<DespachoDto> getByPaciente(int pacienteId) {
        RequestDto req = new RequestDto("Recetas", "getRecetasByPaciente", gson.toJson(pacienteId), null);
        ResponseDto res = sendRequest(req);

        if (res != null && res.isSuccess() && res.getData() != null) {
            try {
                return gson.fromJson(res.getData(), new TypeToken<List<DespachoDto>>() {}.getType());
            } catch (Exception e) {
                System.err.println("[DespachoService] Error al parsear: " + e.getMessage());
            }
        }
        return List.of();
    }

    public boolean updateState(int recetaId, String nuevoEstado) {
        String data = String.format("{\"idReceta\":%d,\"estado\":\"%s\"}", recetaId, nuevoEstado);

        RequestDto req = new RequestDto("Recetas", "updateEstado", data, null);
        ResponseDto res = sendRequest(req);

        return res != null && res.isSuccess();
    }

    public DespachoDto getById(int id) {
        RequestDto req = new RequestDto("Recetas", "getRecetaById", gson.toJson(id), null);
        ResponseDto res = sendRequest(req);

        if (res != null && res.isSuccess() && res.getData() != null) {
            try {
                return gson.fromJson(res.getData(), DespachoDto.class);
            } catch (Exception e) {
                System.err.println("[DespachoService] Error al parsear: " + e.getMessage());
            }
        }
        return null;
    }
}