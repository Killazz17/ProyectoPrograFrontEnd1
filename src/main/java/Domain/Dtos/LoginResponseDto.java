package Domain.Dtos;

import java.io.Serializable;

public class LoginResponseDto implements Serializable {
    private boolean success;
    private String nombre;
    private String rol;
    private String mensaje;

    public LoginResponseDto(boolean success, String nombre, String rol, String mensaje) {
        this.success = success;
        this.nombre = nombre;
        this.rol = rol;
        this.mensaje = mensaje;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRol() {
        return rol;
    }

    public String getMensaje() {
        return mensaje;
    }
}