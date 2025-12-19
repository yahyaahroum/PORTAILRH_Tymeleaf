package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Affaire;
import ma.richebois.gestioninterp.Model.Conge;
import ma.richebois.gestioninterp.Model.Individu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CongeRepository extends JpaRepository<Conge,Long> {

    Integer countByAffaireAndIndividuAndDateDebutAndDateFin(Affaire affaire, Individu individu, Date start, Date end);
    List<Conge> findAllByAffaireInAndEtatInOrderByIdDesc(List<Affaire> affaires,List<String> states);
    List<Conge> findAllByEtatInOrderByIdDesc(List<String> states);

    @Query(value = "select individu.id,individu.matricule,nom,prenom,adresserue,dateentree,date_debut,date_fin,motif,DATEADD(DD,1,date_fin) as date_reprise,exception\n" +
            "from individu,contrat,conge \n" +
            "where contrat.matricule=individu.matricule and contrat.contratactif=2 and conge.individu=individu.id and conge.id=:Id",nativeQuery = true)
    List<Object[]> getIndividuForDemande(@Param("Id") long id);

    Integer countAllByEtat(String state);
    Integer countAllByEtatAndAffaireIn(String state,List<Affaire> affaires);
    Integer countAllByAffaireIn(List<Affaire> affaires);


}
