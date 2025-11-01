package Services;

import Domain.Dtos.HistoricoRecetaDto;
import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class HistoricoRecetaService extends BaseService {

    public HistoricoRecetaService(String host, int port) {
        super(host, port);
    }

    public List<HistoricoRecetaDto> getAll() {
        RequestDto req = new RequestDto("Historico", "getAll", null, null);
        ResponseDto res = sendRequest(req);
        if (res != null && res.isSuccess()) {
            return gson.fromJson(res.getData(), new TypeToken<List<HistoricoRecetaDto>>() {}.getType());
        }
        return List.of();
    }

    public List<HistoricoRecetaDto> searchByFilter(String filtro) {
        RequestDto req = new RequestDto("Historico", "search", gson.toJson(filtro), null);
        ResponseDto res = sendRequest(req);
        if (res != null && res.isSuccess()) {
            return gson.fromJson(res.getData(), new TypeToken<List<HistoricoRecetaDto>>() {}.getType());
        }
        return List.of();
    }
}