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
public interface AjoutRepository extends JpaRepository<Ajout, Long> {

  List<Ajout> findAllByImp(Optional<Import> imp);

  Ajout findByCodecin(@Param("codecin") String codecin);

  List<Ajout> findAllByStateAndOrigineOrderByNomAsc(String state, String origine);

  List<Ajout> findAllByMatriculeOrderByIdDesc(int matricule);

  List<Ajout> findAllByStateInOrderByMatriculeDesc(List<String> state);

  /*
   * @Query(value =
   * "select * from ajout,affaire,fonction where ajout.fonction=fonction.codefonction AND ajout.codechantier=affaire.code AND ajout.id=:Id"
   * ,nativeQuery = true)
   * Ajout getPersonForContract(@Param("Id") Long Id);
   */
  @Query(value = "SELECT a.* FROM ajout a " +
      "JOIN affaire c ON c.code = a.codechantier " +
      "JOIN fonction f ON a.fonction = f.codefonction " +
      "WHERE a.id = :id", nativeQuery = true)
  Ajout getPersonForContract(@Param("id") Long id);

  List<Ajout> findAllByCodechantierInAndOrigineAndStateOrderByNom(List<String> affaireList, String origine,
      String state);

  List<Ajout> findAllByStateInAndOrigineInAndDateentreeBetweenOrderByMatriculeAsc(List<String> states,
      List<String> origine, Date start, Date end);

  Integer countAllByOrigineAndState(String origine, String state);

  Integer countAllByOrigineAndStateAndCodechantierIn(String origine, String state, List<String> chantierList);

  Integer countAllByOrigineAndCodechantierIn(String origine, List<String> chantierList);

  Integer countAllByOrigine(String origine);

  List<Ajout> findAllByCodechantierInAndOrigineOrderByNomAsc(List<String> chantierList, String origine);

  List<Ajout> findAllByOrigineOrderByNomAsc(String origine);

  /**
   * Récupère les nouveaux contrats saisis entre deux dates,
   * en EXCLUANT:
   * 1. Les individus qui ont au moins un contrat avec contratactif=2
   * 2. Les individus dont la date d'entrée existe déjà comme date d'embauche dans
   * les contrats
   * 
   * Cela garantit que seuls les individus dont TOUS les contrats ont
   * contratactif=1 ET dont la date d'entrée est nouvelle sont exportés.
   * Évite ainsi les doublons: une fois qu'un contrat est intégré
   * (contratactif=2) ou qu'une date existe déjà,
   * l'individu n'apparaît plus dans les exports suivants.
   */
  @Query(value = "SELECT DISTINCT a.* FROM ajout a " +
      "WHERE a.etat_individu IN (:states) " +
      "AND a.origine IN (:origine) " +
      "AND a.dateentree BETWEEN :datestart AND :dateend " +
      "AND a.matricule NOT IN ( " +
      "    SELECT DISTINCT c.matricule FROM contrat c WHERE c.contratactif = 2 " +
      ") " +
      "AND NOT EXISTS ( " +
      "    SELECT 1 FROM contrat c2 " +
      "    WHERE c2.matricule = a.matricule " +
      "    AND CAST(c2.dateentree AS DATE) = CAST(a.dateentree AS DATE) " +
      ") " +
      "ORDER BY a.matricule ASC", nativeQuery = true)
  List<Ajout> findNewContractsWithoutIntegratedOnes(
      @Param("states") List<String> states,
      @Param("origine") List<String> origine,
      @Param("datestart") Date datestart,
      @Param("dateend") Date dateend);

}
