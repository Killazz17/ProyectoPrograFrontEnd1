package Services;

import com.google.gson.Gson;
import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import Presentation.Config.ApiConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * BaseService centraliza la conexión al backend usando ApiConfig.
 * Ajustado para usar host/port desde src/main/resources/app.properties
 */
public class BaseService {
    protected final String host;
    protected final int port;
    protected final Gson gson = new Gson();

    public BaseService() {
        this.host = ApiConfig.HOST;
        this.port = ApiConfig.PORT;
    }

    public BaseService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // Método de ejemplo para crear socket (si la app usa sockets)
    protected Socket createSocket() throws IOException {
        return new Socket(host, port);
    }

    /**
     * Envía una solicitud al servidor y espera una respuesta.
     * Establece una conexión temporal, envía el RequestDto como JSON,
     * lee la respuesta y cierra la conexión.
     */
    protected ResponseDto sendRequest(RequestDto request) {
        try (Socket socket = createSocket();
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Serializar y enviar la solicitud
            String jsonRequest = gson.toJson(request);
            out.println(jsonRequest);

            // Leer la respuesta
            String jsonResponse = in.readLine();
            if (jsonResponse != null && !jsonResponse.isEmpty()) {
                return gson.fromJson(jsonResponse, ResponseDto.class);
            }

            return new ResponseDto(false, "Respuesta vacía del servidor", null);

        } catch (IOException e) {
            System.err.println("Error al comunicarse con el servidor: " + e.getMessage());
            return new ResponseDto(false, "Error de conexión: " + e.getMessage(), null);
        }
    }

    // Si en el futuro se usa HTTP, se puede obtener la base URL con ApiConfig.getBaseUrl()
}