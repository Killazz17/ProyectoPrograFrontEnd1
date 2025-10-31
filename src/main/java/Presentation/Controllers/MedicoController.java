package Presentation.Controllers;

import Domain.Dtos.ResponseDto;
import Services.MedicoService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Map;

public class MedicoController {
    private final MedicoService medicoService;
    private final Gson gson = new Gson();

    public MedicoController() {
        this.medicoService = new MedicoService();
    }

    public List<Map<String, Object>> loadAll() {
        ResponseDto response = medicoService.getAll();
        if (response.isSuccess()) {
            return gson.fromJson(response.getData(),
                    new TypeToken<List<Map<String, Object>>>(){}.getType());
        }
        return null;
    }

    public ResponseDto create(int id, String nombre, String especialidad) {
        return medicoService.create(id, nombre, especialidad);
    }

    public ResponseDto delete(int id) {
        return medicoService.delete(id);
    }
}