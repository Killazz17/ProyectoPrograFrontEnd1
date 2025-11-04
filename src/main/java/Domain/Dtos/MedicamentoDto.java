package Domain.Dtos;

import java.io.Serializable;

public class MedicamentoDto implements Serializable {
    private String codigo;
    private String nombre;
    private String presentacion;  // CORREGIDO: era "descripcion"

    public MedicamentoDto() {}

    public MedicamentoDto(String codigo, String nombre, String presentacion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.presentacion = presentacion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public String getDescripcion() {
        return presentacion;
    }

    public void setDescripcion(String descripcion) {
        this.presentacion = descripcion;
    }

    @Override
    public String toString() {
        return nombre + " (" + codigo + ")";
    }
}