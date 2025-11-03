// src/main/java/Domain/Dtos/RecetaDetalladaDto.java
package Domain.Dtos;

import java.io.Serializable;
import java.util.List;

/**
 * DTO que incluye los medicamentos para el dashboard
 */
public class RecetaDetalladaDto implements Serializable {
    private int id;
    private int idPaciente;
    private int idMedico;
    private String fecha; // formato: yyyy-MM-dd
    private String estado; // CONFECCIONADA, EN_PROCESO, LISTA, ENTREGADA
    private List<MedicamentoPrescritoDto> medicamentos;

    public RecetaDetalladaDto() {}

    public RecetaDetalladaDto(int id, int idPaciente, int idMedico, String fecha,
                              String estado, List<MedicamentoPrescritoDto> medicamentos) {
        this.id = id;
        this.idPaciente = idPaciente;
        this.idMedico = idMedico;
        this.fecha = fecha;
        this.estado = estado;
        this.medicamentos = medicamentos;
    }

    // Getters y setters
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

    public List<MedicamentoPrescritoDto> getMedicamentos() { return medicamentos; }
    public void setMedicamentos(List<MedicamentoPrescritoDto> medicamentos) {
        this.medicamentos = medicamentos;
    }
}