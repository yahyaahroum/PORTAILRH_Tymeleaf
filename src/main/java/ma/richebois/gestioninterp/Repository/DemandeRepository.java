package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Affaire;
import ma.richebois.gestioninterp.Model.Demande;
import ma.richebois.gestioninterp.Model.Diplomes;
import ma.richebois.gestioninterp.Model.Login;
import org.jfree.util.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandeRepository extends JpaRepository<Demande,Long> {

    List<Demande> findAllByEmpOrderByIdDesc(Login login);

    List<Demande> findAllByEmpAndEtatOrderByIdDesc(Login login,String state);
    List<Demande> findAllByEtatOrderByIdDesc(String state);

    Integer countAllByEtat(String state);

    Integer countAllByEtatAndEmp(String state, Login login);

    Integer countAllByEmp(Login login);

    @Query(value = "select demande.id,individu.nom,fonction.libelle,individu.prenom,dateentree,login.matricule,codecin,cnss from demande,contrat,individu,login,fonction\n" +
            "where login.matricule=individu.matricule and contrat.matricule=login.matricule and demande.employe=login.id and contrat.fonction=fonction.id and demande.id=:Id",nativeQuery = true)
    List<Object[]> getDemande(@Param("Id") long id);
}
