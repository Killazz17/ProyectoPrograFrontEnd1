package Domain.Dtos;

import java.io.Serializable;

public class HistoricoRecetaDto implements Serializable {
    private int id;
    private String paciente;
    private String medico;
    private String fecha;
    private String estado;

    public HistoricoRecetaDto() {}

    public HistoricoRecetaDto(int id, String paciente, String medico, String fecha, String estado) {
        this.id = id;
        this.paciente = paciente;
        this.medico = medico;
        this.fecha = fecha;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPaciente() { return paciente; }
    public void setPaciente(String paciente) { this.paciente = paciente; }

    public String getMedico() { return medico; }
    public void setMedico(String medico) { this.medico = medico; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Receta " + id + " - " + paciente + " (" + estado + ")";
    }
}