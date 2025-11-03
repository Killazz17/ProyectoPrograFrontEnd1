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

    /**
     * Obtener todas las recetas (despachos)
     */
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

    /**
     * Obtener recetas por paciente
     */
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

    /**
     * Actualizar estado de una receta
     */
    public boolean updateState(int recetaId, String nuevoEstado) {
        // Crear un objeto con los datos necesarios
        String data = String.format("{\"idReceta\":%d,\"estado\":\"%s\"}", recetaId, nuevoEstado);

        RequestDto req = new RequestDto("Recetas", "updateEstado", data, null);
        ResponseDto res = sendRequest(req);

        return res != null && res.isSuccess();
    }

    /**
     * Buscar receta por ID
     */
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