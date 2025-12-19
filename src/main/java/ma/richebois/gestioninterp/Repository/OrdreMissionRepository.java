package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Login;
import ma.richebois.gestioninterp.Model.OrdreMission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdreMissionRepository extends JpaRepository<OrdreMission, Long> {
    List<OrdreMission> findAllByLoginOrderByIdDesc(Login login);

    List<OrdreMission> findAllByOrderByIdAsc();

    List<OrdreMission> findAllByOrderByIdDesc();
}
