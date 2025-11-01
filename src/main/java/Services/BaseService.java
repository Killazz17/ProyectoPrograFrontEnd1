package Services;

import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class BaseService {
    protected final String host;
    protected final int port;
    protected final Gson gson = new Gson();

    // Constructor por defecto que usa localhost:7070
    public BaseService() {
        this("localhost", 7070);
    }

    public BaseService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    protected ResponseDto sendRequest(RequestDto request) {
        Socket socket = null;
        try {
            socket = new Socket(host, port);
            socket.setSoTimeout(5000); // 5 segundos timeout

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String requestJson = gson.toJson(request);
            System.out.println("[BaseService] Enviando: " + requestJson);

            writer.write(requestJson);
            writer.newLine();
            writer.flush();

            String responseJson = reader.readLine();
            System.out.println("[BaseService] Recibido: " + responseJson);

            if (responseJson == null) {
                return new ResponseDto(false, "No se recibió respuesta del servidor", null);
            }

            return gson.fromJson(responseJson, ResponseDto.class);

        } catch (IOException e) {
            System.err.println("[BaseService] Error de conexión: " + e.getMessage());
            return new ResponseDto(false, "Error de conexión: " + e.getMessage(), null);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}