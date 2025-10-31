package Presentation.Controllers;

import Domain.Dtos.ResponseDto;
import Services.PacienteService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PacienteController {
    private final PacienteService pacienteService;
    private final Gson gson = new Gson();

    public PacienteController() {
        this.pacienteService = new PacienteService();
    }

    public List<Map<String, Object>> loadAll() {
        ResponseDto response = pacienteService.getAll();
        if (response.isSuccess()) {
            return gson.fromJson(response.getData(),
                    new TypeToken<List<Map<String, Object>>>(){}.getType());
        }
        return null;
    }

    public ResponseDto create(int id, String nombre, Date fechaNacimiento, String numeroTelefono) {
        return pacienteService.create(id, nombre, fechaNacimiento, numeroTelefono);
    }

    public ResponseDto delete(int id) {
        return pacienteService.delete(id);
    }
}