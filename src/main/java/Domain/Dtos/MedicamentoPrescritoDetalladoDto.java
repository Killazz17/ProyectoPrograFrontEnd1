package Domain.Dtos;

import java.io.Serializable;

/**
 * DTO detallado de medicamento prescrito que incluye información
 * del medicamento base y la fecha de confección
 */
public class MedicamentoPrescritoDetalladoDto implements Serializable {
    private int id;
    private String medicamentoCodigo;
    private String medicamentoNombre;
    private String medicamentoPresentacion;
    private int cantidad;
    private int duracion;
    private String indicaciones;
    private String fechaConfeccion; // De la receta asociada
    private String estado; // Del estado de la receta

    public MedicamentoPrescritoDetalladoDto() {}

    public MedicamentoPrescritoDetalladoDto(int id, String medicamentoCodigo,
                                            String medicamentoNombre, String medicamentoPresentacion,
                                            int cantidad, int duracion, String indicaciones,
                                            String fechaConfeccion, String estado) {
        this.id = id;
        this.medicamentoCodigo = medicamentoCodigo;
        this.medicamentoNombre = medicamentoNombre;
        this.medicamentoPresentacion = medicamentoPresentacion;
        this.cantidad = cantidad;
        this.duracion = duracion;
        this.indicaciones = indicaciones;
        this.fechaConfeccion = fechaConfeccion;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMedicamentoCodigo() { return medicamentoCodigo; }
    public void setMedicamentoCodigo(String medicamentoCodigo) {
        this.medicamentoCodigo = medicamentoCodigo;
    }

    public String getMedicamentoNombre() { return medicamentoNombre; }
    public void setMedicamentoNombre(String medicamentoNombre) {
        this.medicamentoNombre = medicamentoNombre;
    }

    public String getMedicamentoPresentacion() { return medicamentoPresentacion; }
    public void setMedicamentoPresentacion(String medicamentoPresentacion) {
        this.medicamentoPresentacion = medicamentoPresentacion;
    }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public int getDuracion() { return duracion; }
    public void setDuracion(int duracion) { this.duracion = duracion; }

    public String getIndicaciones() { return indicaciones; }
    public void setIndicaciones(String indicaciones) { this.indicaciones = indicaciones; }

    public String getFechaConfeccion() { return fechaConfeccion; }
    public void setFechaConfeccion(String fechaConfeccion) {
        this.fechaConfeccion = fechaConfeccion;
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "MedicamentoPrescritoDetalladoDto{" +
                "id=" + id +
                ", medicamentoNombre='" + medicamentoNombre + '\'' +
                ", cantidad=" + cantidad +
                ", fechaConfeccion='" + fechaConfeccion + '\'' +
                '}';
    }
}