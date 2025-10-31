package Services;

import Domain.Dtos.ResponseDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class AuthService {
    private final ApiClient apiClient;
    private final Gson gson = new Gson();
    private UserSession currentUser;

    public AuthService() {
        this.apiClient = ApiClient.getInstance();
    }

    public ResponseDto login(int id, String clave) {
        JsonObject loginData = new JsonObject();
        loginData.addProperty("id", id);
        loginData.addProperty("clave", clave);

        ResponseDto response = apiClient.sendRequest("Auth", "login", gson.toJson(loginData));

        if (response.isSuccess()) {
            JsonObject userData = gson.fromJson(response.getData(), JsonObject.class);
            currentUser = new UserSession(
                    userData.get("id").getAsInt(),
                    userData.get("nombre").getAsString(),
                    userData.get("rol").getAsString()
            );
        }

        return response;
    }

    public UserSession getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public static class UserSession {
        private final int id;
        private final String nombre;
        private final String rol;

        public UserSession(int id, String nombre, String rol) {
            this.id = id;
            this.nombre = nombre;
            this.rol = rol;
        }

        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getRol() { return rol; }

        public boolean isAdmin() { return "ADMINISTRADOR".equals(rol); }
        public boolean isMedico() { return "MEDICO".equals(rol); }
        public boolean isFarmaceuta() { return "FARMACEUTA".equals(rol); }
        public boolean isPaciente() { return "PACIENTE".equals(rol); }
    }
}
