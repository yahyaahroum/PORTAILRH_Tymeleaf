package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Decharge;
import ma.richebois.gestioninterp.Model.DechargePJ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DechargePJRepository extends JpaRepository<DechargePJ,Long> {

    List<DechargePJ> findAllByDecharge(Decharge decharge);
}
