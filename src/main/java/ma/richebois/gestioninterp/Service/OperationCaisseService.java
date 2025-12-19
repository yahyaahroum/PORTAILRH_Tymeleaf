package ma.richebois.gestioninterp.Service;

import ma.richebois.gestioninterp.DTO.AvanceDTO;
import ma.richebois.gestioninterp.Model.OperationCaisse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.util.List;

public interface OperationCaisseService {

    public List<OperationCaisse> getAll();
    public List<OperationCaisse> getAllByDate(String start,String end) throws ParseException;
    public OperationCaisse save(OperationCaisse op);
    public Page<OperationCaisse> findPaginatedOperation(Pageable pageable);

    public List<AvanceDTO>  traitementAvance(String dateStart, String dateEnd) throws ParseException;
}
