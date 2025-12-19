package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Ville;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VilleRepository extends JpaRepository<Ville,Long> {
}
