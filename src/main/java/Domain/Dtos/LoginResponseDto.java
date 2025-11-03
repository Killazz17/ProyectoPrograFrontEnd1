package Domain.Dtos;

import java.io.Serializable;

public class LoginResponseDto implements Serializable {
    private boolean success;
    private String nombre;
    private String rol;
    private String mensaje;

    public LoginResponseDto() {}

    public LoginResponseDto(boolean success, String nombre, String rol, String mensaje) {
        this.success = success;
        this.nombre = nombre;
        this.rol = rol;
        this.mensaje = mensaje;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}