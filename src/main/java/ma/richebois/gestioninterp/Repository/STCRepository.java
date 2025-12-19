package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Affaire;
import ma.richebois.gestioninterp.Model.Individu;
import ma.richebois.gestioninterp.Model.STC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface STCRepository extends JpaRepository<STC,Long> {

    List<STC> findAllBylistNoir(Boolean etat);
    List<STC>  findAllByStateInAndAffaireInOrderByDatedepartAsc(List<String> states,List<Affaire> affaireList);
    List<STC>  findAllByStateInAndDatedepartBetweenOrderByDatedepartAsc(List<String> states,Date start,Date end);
    List<STC>  findAllByStateInOrderByDatedepartAsc(List<String> states);

    Integer countAllByState(String state);
    Integer countAllByStateAndAffaireIn(String state,List<Affaire> affaires);
    Integer countAllByAffaireIn(List<Affaire> affaires);

    Integer countByIndividuAndAffaireAndDatedepart(Individu individu,Affaire affaire, Date datedepart);




}
