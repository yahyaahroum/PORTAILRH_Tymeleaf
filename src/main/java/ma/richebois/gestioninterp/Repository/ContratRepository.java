package ma.richebois.gestioninterp.Repository;


import ma.richebois.gestioninterp.Model.Contrat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ContratRepository extends JpaRepository<Contrat,Long> {

    List<Contrat> findAllByMatriculeOrderByNumcontratDesc(int matricule);
    List<Contrat> findAllByMatriculeAndDateembaucheOrderByNumcontratDesc(int matricule, Date dateentree);

    List<Contrat> findAllByMatriculeAndDatesortieAndProfilecodeNotIn(int matricule,Date datesortie,List<String> CodeProfile);

    List<Contrat> findAllByContratactif(int contratActif);

    List<Contrat> findAllByContratactifAndProfilecode(int contratActif,String profile);





}
