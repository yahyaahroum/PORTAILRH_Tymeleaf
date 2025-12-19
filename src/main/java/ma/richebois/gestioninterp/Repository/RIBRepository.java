package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.RIB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RIBRepository extends JpaRepository<RIB,Long> {

    List<RIB> findAllByIndividuOrderByCodeRibDesc(int matricule);

}
