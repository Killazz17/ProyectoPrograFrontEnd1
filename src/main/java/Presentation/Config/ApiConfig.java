package Presentation.Config;

import java.io.InputStream;
import java.util.Properties;

public class ApiConfig {
    private static final String PROPERTIES_FILE = "/app.properties";
    public static String HOST = "localhost";
    public static int PORT = 7070;

    static {
        try (InputStream in = ApiConfig.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (in != null) {
                Properties p = new Properties();
                p.load(in);
                HOST = p.getProperty("api.host", HOST);
                PORT = Integer.parseInt(p.getProperty("api.port", String.valueOf(PORT)));
            }
        } catch (Exception e) {
            System.err.println("No se pudo leer " + PROPERTIES_FILE + ", usando valores por defecto: " + e.getMessage());
        }
    }

    public static String getBaseUrl() {
        return "http://" + HOST + ":" + PORT;
    }
}