package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Fichiers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FichierRepository extends JpaRepository<Fichiers,Long> {
}
