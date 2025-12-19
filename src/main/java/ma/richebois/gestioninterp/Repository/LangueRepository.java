package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Langue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LangueRepository extends JpaRepository<Langue,Long> {
}
