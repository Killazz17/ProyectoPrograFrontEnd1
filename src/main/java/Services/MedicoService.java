package Services;

import Domain.Dtos.ResponseDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MedicoService {
    private final ApiClient apiClient;
    private final Gson gson = new Gson();

    public MedicoService() {
        this.apiClient = ApiClient.getInstance();
    }

    public ResponseDto getAll() {
        return apiClient.sendRequest("Medicos", "getAllMedicos", null);
    }

    public ResponseDto create(int id, String nombre, String especialidad) {
        JsonObject data = new JsonObject();
        data.addProperty("id", id);
        data.addProperty("nombre", nombre);
        data.addProperty("especialidad", especialidad);

        return apiClient.sendRequest("Medicos", "createMedico", gson.toJson(data));
    }

    public ResponseDto delete(int id) {
        return apiClient.sendRequest("Medicos", "deleteMedico", String.valueOf(id));
    }
}