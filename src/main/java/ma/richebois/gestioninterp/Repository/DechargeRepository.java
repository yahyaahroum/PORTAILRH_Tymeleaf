package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Decharge;
import ma.richebois.gestioninterp.Model.Individu;
import ma.richebois.gestioninterp.Model.Materiel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DechargeRepository extends JpaRepository<Decharge,Long> {
    List<Decharge> findAllByIndividuAndStatutInOrderByDateDechargeDesc(Individu individu,List<String> statuts);

    List<Decharge> findAllByStatutOrderByIdDesc(String statut);
}
