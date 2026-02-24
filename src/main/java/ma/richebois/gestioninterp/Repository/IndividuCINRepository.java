package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.IndividuCIN;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndividuCINRepository extends JpaRepository<IndividuCIN, String> {

        @Query("SELECT i FROM IndividuCIN i WHERE " +
                        "(:matricule IS NULL OR LOWER(i.matricule) LIKE LOWER(CONCAT('%', :matricule, '%'))) AND " +
                        "(:nom IS NULL OR LOWER(i.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
                        "(:prenom IS NULL OR LOWER(i.prenom) LIKE LOWER(CONCAT('%', :prenom, '%'))) AND " +
                        "(:cin IS NULL OR LOWER(i.cin) LIKE LOWER(CONCAT('%', :cin, '%')))")
        Page<IndividuCIN> search(@Param("matricule") String matricule,
                        @Param("nom") String nom,
                        @Param("prenom") String prenom,
                        @Param("cin") String cin,
                        Pageable pageable);

        @Query("SELECT i FROM IndividuCIN i WHERE " +
                        "(:matricule IS NULL OR LOWER(i.matricule) LIKE LOWER(CONCAT('%', :matricule, '%'))) AND " +
                        "(:nom IS NULL OR LOWER(i.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
                        "(:prenom IS NULL OR LOWER(i.prenom) LIKE LOWER(CONCAT('%', :prenom, '%'))) AND " +
                        "(:cin IS NULL OR LOWER(i.cin) LIKE LOWER(CONCAT('%', :cin, '%')))")
        List<IndividuCIN> findAllFiltered(@Param("matricule") String matricule,
                        @Param("nom") String nom,
                        @Param("prenom") String prenom,
                        @Param("cin") String cin);
}
