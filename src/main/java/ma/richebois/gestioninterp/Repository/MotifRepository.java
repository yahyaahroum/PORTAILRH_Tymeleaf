package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Motif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MotifRepository extends JpaRepository<Motif,Long> {
}
