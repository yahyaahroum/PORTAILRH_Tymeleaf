package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportRepository extends JpaRepository<Import,Long> {
}
