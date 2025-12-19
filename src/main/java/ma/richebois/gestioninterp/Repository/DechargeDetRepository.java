package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Decharge;
import ma.richebois.gestioninterp.Model.DechargeDet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DechargeDetRepository extends JpaRepository<DechargeDet,Long> {

    List<DechargeDet> findAllByDecharge(Decharge decharge);
}
