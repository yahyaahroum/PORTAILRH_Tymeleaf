package ma.richebois.gestioninterp.Service;

import ma.richebois.gestioninterp.Model.Inspecteur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface InspecteurService {
    public Inspecteur saveInspecteur(Inspecteur insp);
    public Inspecteur updateInspecteur(Inspecteur insp,Long id);
    public List<Inspecteur> getAllInspecteur();
    public Optional<Inspecteur> findByIdInspecteur(Long id);
    public void deleteInspecteur(Long id);
    public Page<Inspecteur> findPaginatedInspecteurs(Pageable pageable);
}
