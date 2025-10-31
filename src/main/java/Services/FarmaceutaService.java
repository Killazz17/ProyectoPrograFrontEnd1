package Services;

import Domain.Dtos.ResponseDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class FarmaceutaService {
    private final ApiClient apiClient;
    private final Gson gson = new Gson();

    public FarmaceutaService() {
        this.apiClient = ApiClient.getInstance();
    }

    public ResponseDto getAll() {
        return apiClient.sendRequest("Farmaceutas", "getAllFarmaceutas", null);
    }

    public ResponseDto create(int id, String nombre) {
        JsonObject data = new JsonObject();
        data.addProperty("id", id);
        data.addProperty("nombre", nombre);

        return apiClient.sendRequest("Farmaceutas", "createFarmaceuta", gson.toJson(data));
    }

    public ResponseDto delete(int id) {
        return apiClient.sendRequest("Farmaceutas", "deleteFarmaceuta", String.valueOf(id));
    }
}