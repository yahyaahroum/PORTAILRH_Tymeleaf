package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Sanction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SanctionRepository extends JpaRepository<Sanction,Long> {

    List<Sanction> findAllByOrderByIdDesc();
    List<Sanction> findAllBytypeSanctionOrderByIdDesc(String type);

 @Query(value = "select * from sanction where id_login=:Id and type_sanction=:typeSanction order by id desc",nativeQuery = true)
    List<Sanction> getSanctionsbyLogin_Type(@Param("Id") Long Id,@Param("typeSanction") String typeSanction);

}

