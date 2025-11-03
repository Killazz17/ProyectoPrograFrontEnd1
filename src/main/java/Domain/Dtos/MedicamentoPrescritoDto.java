package Domain.Dtos;

import java.io.Serializable;

public class MedicamentoPrescritoDto implements Serializable {
    private String codigo;
    private int cantidad;
    private int duracion;
    private String indicaciones;

    private transient String nombre;
    private transient String presentacion;

    public MedicamentoPrescritoDto() {}

    public MedicamentoPrescritoDto(String codigo, int cantidad, int duracion, String indicaciones) {
        this.codigo = codigo;
        this.cantidad = cantidad;
        this.duracion = duracion;
        this.indicaciones = indicaciones;
    }

    // Getters y setters
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
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

    @Override
    public String toString() {
        return "MedicamentoPrescritoDto{" +
                "codigo='" + codigo + '\'' +
                ", cantidad=" + cantidad +
                ", duracion=" + duracion +
                ", indicaciones='" + indicaciones + '\'' +
                ", nombre='" + nombre + '\'' +
                ", presentacion='" + presentacion + '\'' +
                '}';
    }
}