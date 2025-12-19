package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Materiel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterielRepository extends JpaRepository<Materiel,Long> {


}
