package hospital.Cliente;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.swing.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WebSocketClienteMensajeria extends WebSocketClient {

    private final Gson gson;
    private Consumer<List<String>> onUsuariosActualizados;
    private Consumer<JsonObject> onMensajeRecibido;
    private String userId;
    private String userName;

    public WebSocketClienteMensajeria(URI serverUri) {
        super(serverUri);
        this.gson = new Gson();
    }

    public void configurarCallbacks(
            Consumer<List<String>> onUsuariosActualizados,
            Consumer<JsonObject> onMensajeRecibido) {
        this.onUsuariosActualizados = onUsuariosActualizados;
        this.onMensajeRecibido = onMensajeRecibido;
    }

    public void registrarUsuario(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;

        JsonObject registro = new JsonObject();
        registro.addProperty("tipo", "REGISTRO");
        registro.addProperty("userId", userId);
        registro.addProperty("userName", userName);

        send(gson.toJson(registro));
    }

    public void enviarMensaje(String destinatarioId, String contenido) {
        JsonObject mensaje = new JsonObject();
        mensaje.addProperty("tipo", "MENSAJE");
        mensaje.addProperty("remitenteId", userId);
        mensaje.addProperty("remitenteNombre", userName);
        mensaje.addProperty("destinatarioId", destinatarioId);
        mensaje.addProperty("contenido", contenido);
        mensaje.addProperty("fechaEnvio", java.time.LocalDateTime.now().toString());

        send(gson.toJson(mensaje));
    }

    public void solicitarUsuarios() {
        JsonObject solicitud = new JsonObject();
        solicitud.addProperty("tipo", "SOLICITAR_USUARIOS");
        send(gson.toJson(solicitud));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Conectado al servidor WebSocket");
    }

    @Override
    public void onMessage(String message) {
        try {
            JsonObject json = gson.fromJson(message, JsonObject.class);
            String tipo = json.get("tipo").getAsString();

            SwingUtilities.invokeLater(() -> {
                switch (tipo) {
                    case "REGISTRO_OK":
                        System.out.println("Registro exitoso");
                        solicitarUsuarios();
                        break;

                    case "LISTA_USUARIOS":
                    case "ACTUALIZACION_USUARIOS":
                        List<String> usuarios = new ArrayList<>();
                        json.getAsJsonArray("usuarios").forEach(element -> {
                            String user = element.getAsString();
                            if (!user.equals(userId)) {
                                usuarios.add(user);
                            }
                        });
                        if (onUsuariosActualizados != null) {
                            onUsuariosActualizados.accept(usuarios);
                        }
                        break;

                    case "MENSAJE_RECIBIDO":
                        if (onMensajeRecibido != null) {
                            onMensajeRecibido.accept(json);
                        }
                        break;
                }
            });
        } catch (Exception e) {
            System.err.println("Error procesando mensaje del servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Desconectado del servidor: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("Error en conexión WebSocket: " + ex.getMessage());
        ex.printStackTrace();
    }
}