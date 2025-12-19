package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Diplomes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiplomeRepository extends JpaRepository<Diplomes,Long> {
}
