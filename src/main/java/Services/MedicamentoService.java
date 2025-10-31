package Services;

import Domain.Dtos.ResponseDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MedicamentoService {
    private final ApiClient apiClient;
    private final Gson gson = new Gson();

    public MedicamentoService() {
        this.apiClient = ApiClient.getInstance();
    }

    public ResponseDto getAll() {
        return apiClient.sendRequest("Medicamentos", "getAllMedicamentos", null);
    }

    public ResponseDto create(String codigo, String nombre, String presentacion) {
        JsonObject data = new JsonObject();
        data.addProperty("codigo", codigo);
        data.addProperty("nombre", nombre);
        data.addProperty("presentacion", presentacion);

        return apiClient.sendRequest("Medicamentos", "createMedicamento", gson.toJson(data));
    }

    public ResponseDto delete(String codigo) {
        return apiClient.sendRequest("Medicamentos", "deleteMedicamento", codigo);
    }
}
