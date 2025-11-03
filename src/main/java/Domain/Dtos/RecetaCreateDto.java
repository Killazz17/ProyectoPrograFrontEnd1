package Domain.Dtos;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * DTO para crear una nueva receta desde el frontend
 * Debe coincidir con RecetaCreateDto del backend
 */
public class RecetaCreateDto implements Serializable {
    private int pacienteId;
    private Date fechaRetiro;
    private List<MedicamentoPrescritoDto> medicamentos;

    public RecetaCreateDto() {}

    public RecetaCreateDto(int pacienteId, Date fechaRetiro, List<MedicamentoPrescritoDto> medicamentos) {
        this.pacienteId = pacienteId;
        this.fechaRetiro = fechaRetiro;
        this.medicamentos = medicamentos;
    }

    public int getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(int pacienteId) {
        this.pacienteId = pacienteId;
    }

    public Date getFechaRetiro() {
        return fechaRetiro;
    }

    public void setFechaRetiro(Date fechaRetiro) {
        this.fechaRetiro = fechaRetiro;
    }

    public List<MedicamentoPrescritoDto> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<MedicamentoPrescritoDto> medicamentos) {
        this.medicamentos = medicamentos;
    }

    @Override
    public String toString() {
        return "RecetaCreateDto{" +
                "pacienteId=" + pacienteId +
                ", fechaRetiro=" + fechaRetiro +
                ", medicamentos=" + (medicamentos != null ? medicamentos.size() : 0) +
                '}';
    }
}