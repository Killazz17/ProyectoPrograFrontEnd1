package Presentation.Controllers;

import Domain.Dtos.ResponseDto;
import Services.MedicamentoService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Map;

public class MedicamentoController {
    private final MedicamentoService medicamentoService;
    private final Gson gson = new Gson();

    public MedicamentoController() {
        this.medicamentoService = new MedicamentoService();
    }

    public List<Map<String, Object>> loadAll() {
        ResponseDto response = medicamentoService.getAll();
        if (response.isSuccess()) {
            return gson.fromJson(response.getData(),
                    new TypeToken<List<Map<String, Object>>>(){}.getType());
        }
        return null;
    }

    public ResponseDto create(String codigo, String nombre, String presentacion) {
        return medicamentoService.create(codigo, nombre, presentacion);
    }

    public ResponseDto delete(String codigo) {
        return medicamentoService.delete(codigo);
    }
}