package Domain.Dtos;

import java.io.Serializable;

public class LoginRequestDto implements Serializable {
    private int id;
    private String clave;

    public LoginRequestDto(int id, String clave) {
        this.id = id;
        this.clave = clave;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
}