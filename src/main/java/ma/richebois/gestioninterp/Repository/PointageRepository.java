package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Affaire;
import ma.richebois.gestioninterp.Model.Individu;
import ma.richebois.gestioninterp.Model.Pointage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PointageRepository extends JpaRepository<Pointage,Long> {


    Page<Pointage> findAllByAffaireInOrderByDatePointageDesc(Pageable pageable, List<Affaire> affaires);
    Page<Pointage> findByOrderByDatePointageDesc(Pageable pageable);
    Page<Pointage> findAllByAffaireAndDatePointageBetween(Pageable pageable,Affaire affaires, Date start,Date end);
    Page<Pointage> findAllByDatePointageBetween(Pageable pageable, Date start,Date end);
    List<Pointage> findAllByAffaireAndDatePointageAndEmp(Affaire affaire, Date datePointage, Individu emp);

    @Query("SELECT COUNT(DISTINCT p.emp) FROM Pointage p where p.affaire in (:listChantier)")
    int countByPersonne(List<Affaire> listChantier);

    @Query("SELECT COUNT(DISTINCT p.emp) FROM Pointage p where p.affaire in (:listChantier) and CAST(date_pointage AS date)=CAST(GETDATE() as date)")
    int countByPersonneTODAY(List<Affaire> listChantier);

    @Query("SELECT COUNT(DISTINCT p.emp) FROM Pointage p")
    int countByAllPersonne();

    @Query("SELECT COUNT(DISTINCT p.emp) FROM Pointage p where CAST(date_pointage AS date)=CAST(GETDATE() as date)")
    int countByAllPersonneTODAY();

    Pointage findByEmpAndDatePointage(Individu individu,Date date);
    List<Pointage> findAllByEmpAndDatePointageBetween(Individu emp,Date start,Date end);

}
