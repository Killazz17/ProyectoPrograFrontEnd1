// src/main/java/Domain/Dtos/RecetaDetalladaDto.java
package Domain.Dtos;

import java.io.Serializable;
import java.util.List;

/**
 * DTO que incluye los medicamentos para el dashboard
 * ACTUALIZADO: Coincide con RecetaDetalladaResponseDto del backend
 */
public class RecetaDetalladaDto implements Serializable {
    private int id;
    private int idPaciente;
    private int idMedico;
    private String fechaConfeccion;
    private String fechaRetiro;
    private String estado;
    private List<MedicamentoPrescritoDto> medicamentos;

    private String pacienteNombre;

    public RecetaDetalladaDto() {}

    public RecetaDetalladaDto(int id, int idPaciente, int idMedico,
                              String fechaConfeccion, String fechaRetiro,
                              String estado, List<MedicamentoPrescritoDto> medicamentos) {
        this.id = id;
        this.idPaciente = idPaciente;
        this.idMedico = idMedico;
        this.fechaConfeccion = fechaConfeccion;
        this.fechaRetiro = fechaRetiro;
        this.estado = estado;
        this.medicamentos = medicamentos;
    }

    // === GETTERS Y SETTERS ===
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }

    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }

    public String getFechaConfeccion() { return fechaConfeccion; }
    public void setFechaConfeccion(String fechaConfeccion) { this.fechaConfeccion = fechaConfeccion; }

    public String getFechaRetiro() { return fechaRetiro; }
    public void setFechaRetiro(String fechaRetiro) { this.fechaRetiro = fechaRetiro; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<MedicamentoPrescritoDto> getMedicamentos() { return medicamentos; }
    public void setMedicamentos(List<MedicamentoPrescritoDto> medicamentos) {
        this.medicamentos = medicamentos;
    }

    public String getPacienteNombre() {
        return pacienteNombre != null ? pacienteNombre : "Desconocido";
    }

    public void setPacienteNombre(String pacienteNombre) {
        this.pacienteNombre = pacienteNombre;
    }
}