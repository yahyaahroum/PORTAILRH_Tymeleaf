package ma.richebois.gestioninterp.Service;

import ma.richebois.gestioninterp.Model.AT;
import ma.richebois.gestioninterp.Model.Affaire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ATService {

    public AT saveAT(AT at,long id);

    public AT updateAT(AT at,long id);
    public List<AT> listAT();
    public Page<AT> findPaginatedAT(Pageable pageable);

    public void deleteAT(long id);
}
