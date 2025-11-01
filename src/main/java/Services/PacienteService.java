package Services;

import Domain.Dtos.PacienteDto;
import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class PacienteService extends BaseService {

    public PacienteService() {
        super(); // Usa localhost:7070 por defecto
    }

    public PacienteService(String host, int port) {
        super(host, port);
    }

    public List<PacienteDto> getAll() {
        // IMPORTANTE: El backend espera "Pacientes" (plural)
        RequestDto req = new RequestDto("Pacientes", "getAllPacientes", null, null);
        ResponseDto res = sendRequest(req);
        if (res != null && res.isSuccess() && res.getData() != null) {
            try {
                return gson.fromJson(res.getData(), new TypeToken<List<PacienteDto>>() {}.getType());
            } catch (Exception e) {
                System.err.println("[PacienteService] Error al parsear lista: " + e.getMessage());
            }
        }
        return List.of();
    }

    public PacienteDto create(int id, String nombre, String fechaNacimiento, String telefono) {
        PacienteDto dto = new PacienteDto(id, nombre, fechaNacimiento, telefono);
        String json = gson.toJson(dto);
        RequestDto req = new RequestDto("Pacientes", "createPaciente", json, null);
        ResponseDto res = sendRequest(req);

        if (res != null && res.isSuccess()) {
            return dto; // El backend solo confirma, devolvemos el DTO original
        }
        return null;
    }

    public boolean delete(int id) {
        RequestDto req = new RequestDto("Pacientes", "deletePaciente", gson.toJson(id), null);
        ResponseDto res = sendRequest(req);
        return res != null && res.isSuccess();
    }

    public List<PacienteDto> searchByName(String nombre) {
        // El backend no tiene este endpoint aún, simulamos búsqueda local
        List<PacienteDto> todos = getAll();
        return todos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }
}