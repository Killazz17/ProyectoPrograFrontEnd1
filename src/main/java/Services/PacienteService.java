package Services;

import Domain.Dtos.ResponseDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.Date;

public class PacienteService {
    private final ApiClient apiClient;
    private final Gson gson = new Gson();

    public PacienteService() {
        this.apiClient = ApiClient.getInstance();
    }

    public ResponseDto getAll() {
        return apiClient.sendRequest("Pacientes", "getAllPacientes", null);
    }

    public ResponseDto create(int id, String nombre, Date fechaNacimiento, String numeroTelefono) {
        JsonObject data = new JsonObject();
        data.addProperty("id", id);
        data.addProperty("nombre", nombre);
        data.addProperty("fechaNacimiento", fechaNacimiento.getTime());
        data.addProperty("numeroTelefono", numeroTelefono);

        return apiClient.sendRequest("Pacientes", "createPaciente", gson.toJson(data));
    }

    public ResponseDto delete(int id) {
        return apiClient.sendRequest("Pacientes", "deletePaciente", String.valueOf(id));
    }
}