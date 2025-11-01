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
                return gson.fromJson(res.getData(), LoginResponseDto.class);
            } catch (Exception e) {
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