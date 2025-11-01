package Services;

import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import Domain.Dtos.LoginRequestDto;
import Domain.Dtos.LoginResponseDto;

public class AuthService extends BaseService {

    public AuthService() {
        super(); // Usa localhost:7070 por defecto
    }

    public AuthService(String host, int port) {
        super(host, port);
    }

    /**
     * Envía las credenciales al backend y devuelve un LoginResponseDto
     */
    public LoginResponseDto login(LoginRequestDto credentials) {
        // Convertimos el objeto a JSON antes de enviarlo
        String jsonData = gson.toJson(credentials);
        RequestDto req = new RequestDto("Auth", "login", jsonData, null);

        ResponseDto res = sendRequest(req);
        if (res == null) {
            return new LoginResponseDto(false, "", "", "No se pudo conectar con el servidor");
        }

        // Si la respuesta general fue exitosa, intentamos convertir los datos a LoginResponseDto
        if (res.isSuccess() && res.getData() != null && !res.getData().isEmpty()) {
            try {
                // El backend devuelve UserResponseDto, así que lo mapeamos a LoginResponseDto
                com.google.gson.JsonObject jsonObject = gson.fromJson(res.getData(), com.google.gson.JsonObject.class);

                String nombre = jsonObject.get("nombre").getAsString();
                String rol = jsonObject.get("rol").getAsString();

                return new LoginResponseDto(true, nombre, rol, "Login exitoso");
            } catch (Exception e) {
                System.err.println("[AuthService] Error al interpretar respuesta: " + e.getMessage());
                return new LoginResponseDto(false, "", "", "Error al interpretar la respuesta del servidor");
            }
        }

        // Si hubo error, devolvemos el mensaje del ResponseDto
        return new LoginResponseDto(false, "", "", res.getMessage());
    }

    /**
     * Envía solicitud de cierre de sesión
     */
    public boolean logout(String token) {
        RequestDto req = new RequestDto("Auth", "logout", null, token);
        ResponseDto res = sendRequest(req);
        return res != null && res.isSuccess();
    }
}