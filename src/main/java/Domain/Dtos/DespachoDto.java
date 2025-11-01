package Domain.Dtos;

import java.io.Serializable;

public class DespachoDto implements Serializable {
    private int id;
    private int idReceta;
    private String estado; // Ej: "CONFECCIONADA", "EN_PROCESO", "LISTA", "ENTREGADA"

    public DespachoDto() {}

    public DespachoDto(int id, int idReceta, String estado) {
        this.id = id;
        this.idReceta = idReceta;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdReceta() { return idReceta; }
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Despacho " + id + " - Receta " + idReceta + " (" + estado + ")";
    }
}