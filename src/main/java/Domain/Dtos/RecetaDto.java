package Domain.Dtos;

import java.io.Serializable;
import java.time.LocalDate;

public class RecetaDto implements Serializable {
    private int id;
    private int idPaciente;
    private int idMedico;
    private String fecha;
    private String estado;
    private String observaciones;

    public RecetaDto() {}

    public RecetaDto(int id, int idPaciente, int idMedico, String fecha, String estado, String observaciones) {
        this.id = id;
        this.idPaciente = idPaciente;
        this.idMedico = idMedico;
        this.fecha = fecha;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }

    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    @Override
    public String toString() {
        return "Receta N°" + id + " - Paciente " + idPaciente + " - Médico " + idMedico;
    }
}