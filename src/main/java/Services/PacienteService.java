package Services;

import Domain.Dtos.PacienteDto;
import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.stream.Collectors;

public class PacienteService extends BaseService {

    public PacienteService() {
        super();
    }

    public PacienteService(String host, int port) {
        super(host, port);
    }

    public List<PacienteDto> getAll() {
        RequestDto req = new RequestDto("Pacientes", "getAllPacientes", null, null);
        ResponseDto res = sendRequest(req);
        if (res != null && res.isSuccess() && res.getData() != null) {
            try {
                return gson.fromJson(res.getData(), new TypeToken<List<PacienteDto>>() {}.getType());
            } catch (Exception e) {
                System.err.println("[PacienteService] Error al parsear lista: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return List.of();
    }

    public PacienteDto create(int id, String nombre, String fechaNacimiento, String telefono) {
        // Crear el DTO - el constructor ya maneja la conversión de fecha
        PacienteDto dto = new PacienteDto(id, nombre, fechaNacimiento, telefono);

        // Serializar a JSON
        String json = gson.toJson(dto);
        System.out.println("[PacienteService] Enviando JSON: " + json);

        RequestDto req = new RequestDto("Pacientes", "createPaciente", json, null);
        ResponseDto res = sendRequest(req);

        if (res != null) {
            System.out.println("[PacienteService] Respuesta del servidor: success=" + res.isSuccess() + ", message=" + res.getMessage());
            if (res.isSuccess()) {
                return dto;
            } else {
                System.err.println("[PacienteService] Error del servidor: " + res.getMessage());
            }
        } else {
            System.err.println("[PacienteService] No se recibió respuesta del servidor");
        }

        return null;
    }

    public boolean delete(int id) {
        System.out.println("[PacienteService] Eliminando paciente con ID: " + id);

        // El backend espera el ID como String en el data
        RequestDto req = new RequestDto("Pacientes", "deletePaciente", String.valueOf(id), null);
        ResponseDto res = sendRequest(req);

        if (res != null) {
            System.out.println("[PacienteService] Respuesta delete: success=" + res.isSuccess() + ", message=" + res.getMessage());
            return res.isSuccess();
        }

        System.err.println("[PacienteService] No se recibió respuesta del servidor");
        return false;
    }

    public List<PacienteDto> searchByName(String nombre) {
        List<PacienteDto> todos = getAll();
        return todos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
    }
}