package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Fonction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FonctionRepository extends JpaRepository<Fonction,Long> {

    Fonction findByCodefonction(String code);

}
