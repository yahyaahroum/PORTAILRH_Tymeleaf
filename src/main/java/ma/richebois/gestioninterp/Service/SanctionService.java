package ma.richebois.gestioninterp.Service;
import ma.richebois.gestioninterp.Model.Sanction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SanctionService {
    public Sanction saveSanction(Sanction sanction);
    public Sanction updateSanction(Sanction sanction,Long id);
    public List<Sanction> getAllSanction();
    public Optional<Sanction> findByIdSanction(Long id);
    public void deleteSanction(Long id);
    public Page<Sanction> findPaginatedSanctions(Pageable pageable,Long Id,String typeSanction);
     
    
}
