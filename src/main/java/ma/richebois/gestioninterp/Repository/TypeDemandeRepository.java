package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Diplomes;
import ma.richebois.gestioninterp.Model.TypeDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeDemandeRepository extends JpaRepository<TypeDemande,Long> {
}
