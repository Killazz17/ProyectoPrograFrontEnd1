package Services;

import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;

public class ApiClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 7070;
    private static final int MESSAGE_PORT = 7001;

    private final Gson gson = new Gson();
    private Socket requestSocket;
    private Socket messageSocket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader messageIn;
    private MessageListener messageListener;
    private static ApiClient instance;

    private ApiClient() {}

    public static ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public boolean connect() {
        try {
            requestSocket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new PrintWriter(requestSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));

            messageSocket = new Socket(SERVER_HOST, MESSAGE_PORT);
            messageIn = new BufferedReader(new InputStreamReader(messageSocket.getInputStream()));

            startMessageListener();
            System.out.println("✓ Conectado al servidor");
            return true;
        } catch (IOException e) {
            System.err.println("Error al conectar: " + e.getMessage());
            return false;
        }
    }

    public ResponseDto sendRequest(String controller, String request, String data) {
        RequestDto requestDto = new RequestDto(controller, request, data, null);
        return sendRequest(requestDto);
    }

    public ResponseDto sendRequest(RequestDto request) {
        try {
            String json = gson.toJson(request);
            out.println(json);
            String response = in.readLine();
            return gson.fromJson(response, ResponseDto.class);
        } catch (IOException e) {
            System.err.println("Error al enviar request: " + e.getMessage());
            return new ResponseDto(false, "Error de conexión: " + e.getMessage(), null);
        }
    }

    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    private void startMessageListener() {
        new Thread(() -> {
            try {
                String message;
                while ((message = messageIn.readLine()) != null) {
                    if (messageListener != null) {
                        String finalMessage = message;
                        javax.swing.SwingUtilities.invokeLater(() ->
                                messageListener.onMessageReceived(finalMessage)
                        );
                    }
                }
            } catch (IOException e) {
                System.err.println("Conexión con servidor de mensajes cerrada");
            }
        }, "MessageListener").start();
    }

    public void disconnect() {
        try {
            if (requestSocket != null) requestSocket.close();
            if (messageSocket != null) messageSocket.close();
            System.out.println("✓ Desconectado del servidor");
        } catch (IOException e) {
            System.err.println("Error al desconectar: " + e.getMessage());
        }
    }

    public interface MessageListener {
        void onMessageReceived(String message);
    }
}