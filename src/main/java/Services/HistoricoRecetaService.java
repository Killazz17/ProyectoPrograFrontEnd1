package Services;

import Domain.Dtos.HistoricoRecetaDto;
import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class HistoricoRecetaService extends BaseService {

    public HistoricoRecetaService() {
        super();
    }

    public HistoricoRecetaService(String host, int port) {
        super(host, port);
    }

    public List<HistoricoRecetaDto> getAll() {
        // El backend no tiene un controlador de Historico aún
        System.out.println("[HistoricoRecetaService] ADVERTENCIA: Backend no tiene controlador Historico");
        return List.of();
    }

    public List<HistoricoRecetaDto> searchByFilter(String filtro) {
        RequestDto req = new RequestDto("Historico", "search", gson.toJson(filtro), null);
        ResponseDto res = sendRequest(req);
        if (res != null && res.isSuccess() && res.getData() != null) {
            try {
                return gson.fromJson(res.getData(), new TypeToken<List<HistoricoRecetaDto>>() {}.getType());
            } catch (Exception e) {
                System.err.println("[HistoricoRecetaService] Error al parsear lista: " + e.getMessage());
            }
        }
        return List.of();
    }
}