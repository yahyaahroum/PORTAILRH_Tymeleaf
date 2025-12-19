package ma.richebois.gestioninterp.Service;

import ma.richebois.gestioninterp.Model.Affaire;
import ma.richebois.gestioninterp.Model.Role;
import ma.richebois.gestioninterp.Model.STC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AffaireService {

    public List<Affaire> listChantierByRole(List<Role> roles);

    public Page<Affaire> findPaginatedAffaire(Pageable pageable);

}
