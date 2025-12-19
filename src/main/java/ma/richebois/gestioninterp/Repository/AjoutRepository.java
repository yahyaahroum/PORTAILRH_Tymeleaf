package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Affaire;
import ma.richebois.gestioninterp.Model.Ajout;
import ma.richebois.gestioninterp.Model.Import;

import ma.richebois.gestioninterp.Model.Individu;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AjoutRepository extends JpaRepository<Ajout,Long> {

    List<Ajout> findAllByImp(Optional<Import> imp);

    Ajout findByCodecin(@Param("codecin") String codecin);

    List<Ajout> findAllByStateAndOrigineOrderByNomAsc(String state,String origine);

    List<Ajout> findAllByMatriculeOrderByIdDesc(int matricule);
    List<Ajout> findAllByStateInOrderByMatriculeDesc(List<String> state);

  /*  @Query(value = "select * from ajout,affaire,fonction where ajout.fonction=fonction.codefonction AND ajout.codechantier=affaire.code AND ajout.id=:Id",nativeQuery = true)
    Ajout getPersonForContract(@Param("Id") Long Id);*/
  @Query(value = "SELECT a.* FROM ajout a " +
          "JOIN affaire c ON c.code = a.codechantier " +
          "JOIN fonction f ON a.fonction = f.codefonction " +
          "WHERE a.id = :id",
          nativeQuery = true)
  Ajout getPersonForContract(@Param("id") Long id);

    List<Ajout> findAllByCodechantierInAndOrigineAndStateOrderByNom(List<String> affaireList,String origine,String state);
    List<Ajout> findAllByStateInAndOrigineInAndDateentreeBetweenOrderByMatriculeAsc(List<String> states,List<String> origine,Date start,Date end);
    Integer countAllByOrigineAndState(String origine,String state);
    Integer countAllByOrigineAndStateAndCodechantierIn(String origine,String state,List<String> chantierList);
    Integer countAllByOrigineAndCodechantierIn(String origine,List<String> chantierList);
    Integer countAllByOrigine(String origine);

    List<Ajout> findAllByCodechantierInAndOrigineOrderByNomAsc(List<String> chantierList,String origine);

    List<Ajout> findAllByOrigineOrderByNomAsc(String origine);

}
