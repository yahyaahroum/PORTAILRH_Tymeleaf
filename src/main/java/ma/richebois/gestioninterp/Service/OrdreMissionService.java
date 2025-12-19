package ma.richebois.gestioninterp.Service;

import ma.richebois.gestioninterp.Model.Login;
import ma.richebois.gestioninterp.Model.OrdreMission;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface OrdreMissionService {

    public OrdreMission saveOrdreMission(OrdreMission ordreMission);
    public List<OrdreMission> getAllOrdreMission();
    public Optional<OrdreMission> getOrdreMission(Long id);
    public OrdreMission updateOrdreMission(OrdreMission ordreMission,Long id);
    public void deleteOrdreMission(Long id);
    public Page<OrdreMission> findPaginatedOrdresMissions(Pageable pageable, Login login);
    public void UpdateEtatImprime(OrdreMission ordreMission,Long id);

}
