package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Individu;
import ma.richebois.gestioninterp.Model.Motif;
import ma.richebois.gestioninterp.Model.OperationCaisse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OperationCaisseRepository extends JpaRepository<OperationCaisse,Long> {

    List<OperationCaisse> findAllByMotif(Motif motif);
    List<OperationCaisse> findAllByDateOperBetween(Date start,Date end);
    Integer countAllByIndividu(Individu individu);

}
