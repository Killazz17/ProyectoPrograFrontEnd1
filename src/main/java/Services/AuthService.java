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
     * Login usando nombre de usuario (NO id numérico)
     */
    public LoginResponseDto loginByNombre(String nombre, String clave) {
        // Crear JSON manualmente con nombre y clave
        String jsonData = String.format("{\"nombre\":\"%s\",\"clave\":\"%s\"}", nombre, clave);
        RequestDto req = new RequestDto("Auth", "loginByNombre", jsonData, null);

        ResponseDto res = sendRequest(req);
        if (res == null) {
            return new LoginResponseDto(false, "", "", "No se pudo conectar con el servidor");
        }

        if (res.isSuccess() && res.getData() != null && !res.getData().isEmpty()) {
            try {
                com.google.gson.JsonObject jsonObject = gson.fromJson(res.getData(), com.google.gson.JsonObject.class);

                String nombreUsuario = jsonObject.get("nombre").getAsString();
                String rol = jsonObject.get("rol").getAsString();

                return new LoginResponseDto(true, nombreUsuario, rol, "Login exitoso");
            } catch (Exception e) {
                System.err.println("[AuthService] Error al interpretar respuesta: " + e.getMessage());
                return new LoginResponseDto(false, "", "", "Error al interpretar la respuesta del servidor");
            }
        }

        return new LoginResponseDto(false, "", "", res.getMessage());
    }

    /**
     * Login usando ID (para compatibilidad)
     */
    public LoginResponseDto login(LoginRequestDto credentials) {
        String jsonData = gson.toJson(credentials);
        RequestDto req = new RequestDto("Auth", "login", jsonData, null);

        ResponseDto res = sendRequest(req);
        if (res == null) {
            return new LoginResponseDto(false, "", "", "No se pudo conectar con el servidor");
        }

        if (res.isSuccess() && res.getData() != null && !res.getData().isEmpty()) {
            try {
                com.google.gson.JsonObject jsonObject = gson.fromJson(res.getData(), com.google.gson.JsonObject.class);

                String nombre = jsonObject.get("nombre").getAsString();
                String rol = jsonObject.get("rol").getAsString();

                return new LoginResponseDto(true, nombre, rol, "Login exitoso");
            } catch (Exception e) {
                System.err.println("[AuthService] Error al interpretar respuesta: " + e.getMessage());
                return new LoginResponseDto(false, "", "", "Error al interpretar la respuesta del servidor");
            }
        }

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