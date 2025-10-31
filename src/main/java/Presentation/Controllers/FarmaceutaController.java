package Presentation.Controllers;

import Domain.Dtos.ResponseDto;
import Services.FarmaceutaService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Map;

public class FarmaceutaController {
    private final FarmaceutaService farmaceutaService;
    private final Gson gson = new Gson();

    public FarmaceutaController() {
        this.farmaceutaService = new FarmaceutaService();
    }

    public List<Map<String, Object>> loadAll() {
        ResponseDto response = farmaceutaService.getAll();
        if (response.isSuccess()) {
            return gson.fromJson(response.getData(),
                    new TypeToken<List<Map<String, Object>>>(){}.getType());
        }
        return null;
    }

    public ResponseDto create(int id, String nombre) {
        return farmaceutaService.create(id, nombre);
    }

    public ResponseDto delete(int id) {
        return farmaceutaService.delete(id);
    }
}