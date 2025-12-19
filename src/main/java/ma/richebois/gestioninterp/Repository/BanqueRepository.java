package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Banque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BanqueRepository extends JpaRepository<Banque,Long> {
}
