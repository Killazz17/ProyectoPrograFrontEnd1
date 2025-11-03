package Domain.Dtos;

import java.io.Serializable;

public class DespachoDto implements Serializable {
    private int id;
    private int idPaciente;
    private String nombrePaciente;
    private String fechaConfeccion;
    private String fechaRetiro;
    private String estado;
    private int cantidadMedicamentos;

    public DespachoDto() {}

    public DespachoDto(int id, int idPaciente, String nombrePaciente, String fechaConfeccion,
                       String fechaRetiro, String estado, int cantidadMedicamentos) {
        this.id = id;
        this.idPaciente = idPaciente;
        this.nombrePaciente = nombrePaciente;
        this.fechaConfeccion = fechaConfeccion;
        this.fechaRetiro = fechaRetiro;
        this.estado = estado;
        this.cantidadMedicamentos = cantidadMedicamentos;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }

    public String getNombrePaciente() { return nombrePaciente; }
    public void setNombrePaciente(String nombrePaciente) { this.nombrePaciente = nombrePaciente; }

    public String getFechaConfeccion() { return fechaConfeccion; }
    public void setFechaConfeccion(String fechaConfeccion) { this.fechaConfeccion = fechaConfeccion; }

    public String getFechaRetiro() { return fechaRetiro; }
    public void setFechaRetiro(String fechaRetiro) { this.fechaRetiro = fechaRetiro; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getCantidadMedicamentos() { return cantidadMedicamentos; }
    public void setCantidadMedicamentos(int cantidadMedicamentos) {
        this.cantidadMedicamentos = cantidadMedicamentos;
    }

    @Override
    public String toString() {
        return "Receta " + id + " - " + nombrePaciente + " (" + estado + ")";
    }
}