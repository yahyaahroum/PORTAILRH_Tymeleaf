package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Individu;
import ma.richebois.gestioninterp.Model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndividuRepository extends JpaRepository<Individu,Long> {



   List<Individu> findAllByCodecinOrderByIndividuDesc(String codecin);

   Individu findAllByIndividuOrderByIndividuDesc(int individu);

   Integer countByIndividu(int individu);

   List<Individu> findAllByIndividuOrCodecinOrderByIndividuDesc(int individu,String codecin);

   @Query(value = "select  DISTINCT individu.id,nom,prenom,adressepays,adresserue," +
           "adresseville,cimr,cnss,codecin,codepays,datenaissance," +
           "individu.matricule,niveauetude,sexe,sitfamiliale,service_code,tele " +
           "from individu,contrat " +
           "where individu.matricule=contrat.matricule and contratactif=2 order by nom ASC\n" , nativeQuery = true)
   List<Individu> getContratActif();

   Optional<Individu> findByIndividu(int individu);

   List<Individu> findAllByServiceOrderByNom(Service service);
}
