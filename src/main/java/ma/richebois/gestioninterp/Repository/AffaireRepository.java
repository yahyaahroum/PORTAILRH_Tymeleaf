package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Affaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AffaireRepository extends JpaRepository<Affaire,Long> {
    Affaire findByCode(String codechantier);

    List<Affaire> findAllByCodeInOrderByCodeAsc(List<String> code);
    List<Affaire> findByEtapeNotInOrderByCodeAsc(List<String> etapes);

	List<Affaire> findByCodeIn(List<String> chantiersParRole);

}
