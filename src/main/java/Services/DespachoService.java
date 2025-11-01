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
        // El backend no tiene un controlador de Despacho aún
        // Este servicio necesita ser implementado en el backend
        System.out.println("[DespachoService] ADVERTENCIA: Backend no tiene controlador Despacho");
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