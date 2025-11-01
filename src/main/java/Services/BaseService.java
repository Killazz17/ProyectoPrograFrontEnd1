package Services;

import com.google.gson.Gson;
import Presentation.Config.ApiConfig;

import java.io.IOException;
import java.io.InputStream;
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

    // Método de ejemplo para crear socket (si la app usa sockets)
    protected Socket createSocket() throws IOException {
        return new Socket(host, port);
    }

    // Si en el futuro se usa HTTP, se puede obtener la base URL con ApiConfig.getBaseUrl()
}