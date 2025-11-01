package Domain.Dtos;

import java.io.Serializable;

public class MedicamentoDto implements Serializable {
    private String codigo;
    private String nombre;
    private String descripcion;

    public MedicamentoDto() {}

    public MedicamentoDto(String codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return nombre + " (" + codigo + ")";
    }
}