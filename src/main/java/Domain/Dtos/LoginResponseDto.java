package Domain.Dtos;

import java.io.Serializable;

public class LoginResponseDto implements Serializable {
    private boolean success;
    private int id;
    private String nombre;
    private String rol;
    private String mensaje;

    public LoginResponseDto() {}

    public LoginResponseDto(boolean success, int id, String nombre, String rol, String mensaje) {
        this.success = success;
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
        this.mensaje = mensaje;
    }

    // Constructor antiguo para compatibilidad
    public LoginResponseDto(boolean success, String nombre, String rol, String mensaje) {
        this.success = success;
        this.id = 0; // ID por defecto
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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