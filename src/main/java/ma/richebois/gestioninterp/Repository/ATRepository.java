package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.AT;
import ma.richebois.gestioninterp.Model.Affaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ATRepository extends JpaRepository<AT,Long> {

    List<AT> findAllByChantierATInOrderByIdDesc(List<Affaire> affaire);
}
