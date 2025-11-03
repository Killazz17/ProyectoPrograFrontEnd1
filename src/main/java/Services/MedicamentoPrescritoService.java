package Services;

import Domain.Dtos.MedicamentoPrescritoDetalladoDto;
import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class MedicamentoPrescritoService extends BaseService {

    public MedicamentoPrescritoService() {
        super();
    }

    public MedicamentoPrescritoService(String host, int port) {
        super(host, port);
    }

    public List<MedicamentoPrescritoDetalladoDto> getAllDetallados() {
        RequestDto req = new RequestDto("MedicamentosPrescritos", "getAllDetallados", null, null);
        ResponseDto res = sendRequest(req);

        if (res != null && res.isSuccess() && res.getData() != null) {
            try {
                List<MedicamentoPrescritoDetalladoDto> resultado = gson.fromJson(
                        res.getData(),
                        new TypeToken<List<MedicamentoPrescritoDetalladoDto>>() {}.getType()
                );

                return resultado;
            } catch (Exception e) {
                System.err.println("[MedicamentoPrescritoService] Error al parsear lista: "
                        + e.getMessage());
                e.printStackTrace();
            }
        }

        return List.of();
    }
}