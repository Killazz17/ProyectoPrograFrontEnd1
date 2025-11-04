package Services;

import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import Domain.Dtos.LoginRequestDto;
import Domain.Dtos.LoginResponseDto;

public class AuthService extends BaseService {

    public AuthService() {
        super();
    }

    public AuthService(String host, int port) {
        super(host, port);
    }

    public LoginResponseDto loginByNombre(String nombre, String clave) {
        String jsonData = String.format("{\"nombre\":\"%s\",\"clave\":\"%s\"}", nombre, clave);
        RequestDto req = new RequestDto("Auth", "loginByNombre", jsonData, null);

        ResponseDto res = sendRequest(req);
        if (res == null) {
            return new LoginResponseDto(false, 0, "", "", "No se pudo conectar con el servidor");
        }

        if (res.isSuccess() && res.getData() != null && !res.getData().isEmpty()) {
            try {
                com.google.gson.JsonObject jsonObject = gson.fromJson(res.getData(), com.google.gson.JsonObject.class);

                int id = jsonObject.get("id").getAsInt();
                String nombreUsuario = jsonObject.get("nombre").getAsString();
                String rol = jsonObject.get("rol").getAsString();

                return new LoginResponseDto(true, id, nombreUsuario, rol, "Login exitoso");
            } catch (Exception e) {
                System.err.println("[AuthService] Error al interpretar respuesta: " + e.getMessage());
                e.printStackTrace();
                return new LoginResponseDto(false, 0, "", "", "Error al interpretar la respuesta del servidor");
            }
        }

        return new LoginResponseDto(false, 0, "", "", res.getMessage());
    }

    public LoginResponseDto login(LoginRequestDto credentials) {
        String jsonData = gson.toJson(credentials);
        RequestDto req = new RequestDto("Auth", "login", jsonData, null);

        ResponseDto res = sendRequest(req);
        if (res == null) {
            return new LoginResponseDto(false, 0, "", "", "No se pudo conectar con el servidor");
        }

        if (res.isSuccess() && res.getData() != null && !res.getData().isEmpty()) {
            try {
                com.google.gson.JsonObject jsonObject = gson.fromJson(res.getData(), com.google.gson.JsonObject.class);

                int id = jsonObject.get("id").getAsInt();
                String nombre = jsonObject.get("nombre").getAsString();
                String rol = jsonObject.get("rol").getAsString();

                return new LoginResponseDto(true, id, nombre, rol, "Login exitoso");
            } catch (Exception e) {
                System.err.println("[AuthService] Error al interpretar respuesta: " + e.getMessage());
                e.printStackTrace();
                return new LoginResponseDto(false, 0, "", "", "Error al interpretar la respuesta del servidor");
            }
        }

        return new LoginResponseDto(false, 0, "", "", res.getMessage());
    }

    public boolean changePassword(String nombreUsuario, String claveActual, String claveNueva) {
        try {
            String jsonData = String.format(
                    "{\"nombreUsuario\":\"%s\",\"claveActual\":\"%s\",\"claveNueva\":\"%s\"}",
                    nombreUsuario, claveActual, claveNueva
            );

            RequestDto req = new RequestDto("Auth", "changePassword", jsonData, null);
            ResponseDto res = sendRequest(req);

            if (res == null) {
                System.err.println("[AuthService] No se recibió respuesta del servidor");
                return false;
            }

            return res.isSuccess();

        } catch (Exception e) {
            System.err.println("[AuthService] Error al cambiar contraseña: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean logout(String token) {
        RequestDto req = new RequestDto("Auth", "logout", null, token);
        ResponseDto res = sendRequest(req);
        return res != null && res.isSuccess();
    }
}