package Services;

import Domain.Dtos.HistoricoRecetaDto;
import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import Domain.Dtos.RecetaDetalladaDto;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoricoRecetaService extends BaseService {

    public HistoricoRecetaService() { super(); }
    public HistoricoRecetaService(String host, int port) { super(host, port); }

    public List<HistoricoRecetaDto> getAll() {
        Map<String, String> filtro = new HashMap<>();
        filtro.put("tipo", "all");
        filtro.put("valor", "");

        RequestDto req = new RequestDto("Recetas", "buscarRecetas", gson.toJson(filtro), null);
        ResponseDto res = sendRequest(req);

        if (res != null && res.isSuccess() && res.getData() != null) {
            try {
                return gson.fromJson(res.getData(), new TypeToken<List<HistoricoRecetaDto>>() {}.getType());
            } catch (Exception e) {
                System.err.println("[HistoricoRecetaService] Error al parsear lista: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return List.of();
    }

    public List<HistoricoRecetaDto> searchByFilter(String tipo, String valor) {
        Map<String, String> filtro = new HashMap<>();
        filtro.put("tipo", tipo);
        filtro.put("valor", valor);

        System.out.println("[HistoricoService] Enviando búsqueda: tipo='" + tipo + "', valor='" + valor + "'");

        RequestDto req = new RequestDto("Recetas", "buscarRecetas", gson.toJson(filtro), null);
        ResponseDto res = sendRequest(req);

        if (res != null && res.isSuccess() && res.getData() != null) {
            try {
                List<HistoricoRecetaDto> resultado = gson.fromJson(res.getData(),
                        new TypeToken<List<HistoricoRecetaDto>>() {}.getType());
                System.out.println("[HistoricoService] Recibidos " + resultado.size() + " resultados");
                return resultado;
            } catch (Exception e) {
                System.err.println("[HistoricoRecetaService] Error al parsear búsqueda: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return List.of();
    }

    // NUEVO: Búsqueda por ID de receta
    public List<HistoricoRecetaDto> buscarPorIdReceta(int idReceta) {
        Map<String, String> filtro = new HashMap<>();
        filtro.put("tipo", "id_receta");
        filtro.put("valor", String.valueOf(idReceta));

        System.out.println("[HistoricoService] Buscando receta por ID: " + idReceta);

        RequestDto req = new RequestDto("Recetas", "buscarRecetas", gson.toJson(filtro), null);
        ResponseDto res = sendRequest(req);

        if (res != null && res.isSuccess() && res.getData() != null) {
            try {
                List<HistoricoRecetaDto> resultado = gson.fromJson(res.getData(),
                        new TypeToken<List<HistoricoRecetaDto>>() {}.getType());
                System.out.println("[HistoricoService] Encontradas " + resultado.size() + " recetas con ID " + idReceta);
                return resultado;
            } catch (Exception e) {
                System.err.println("[HistoricoRecetaService] Error al parsear ID receta: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return List.of();
    }

    public RecetaDetalladaDto getDetalleReceta(int idReceta) {
        RequestDto req = new RequestDto("Recetas", "getRecetaById", gson.toJson(idReceta), null);
        ResponseDto res = sendRequest(req);

        if (res != null && res.isSuccess() && res.getData() != null) {
            try {
                return gson.fromJson(res.getData(), RecetaDetalladaDto.class);
            } catch (Exception e) {
                System.err.println("[HistoricoRecetaService] Error al parsear detalle: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }
}