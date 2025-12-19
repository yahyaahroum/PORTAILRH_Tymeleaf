package ma.richebois.gestioninterp.Service;

import ma.richebois.gestioninterp.Model.Login;
import ma.richebois.gestioninterp.Model.OrdreMission;
import ma.richebois.gestioninterp.Model.Role;
import ma.richebois.gestioninterp.Repository.OrdreMissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class OrdreMissionServiceImpl implements  OrdreMissionService{
  @Autowired
    private OrdreMissionRepository omRepository;
    @Override
    public OrdreMission saveOrdreMission(OrdreMission ordreMission) {
        ordreMission.setImprime(false);
            return omRepository.save(ordreMission);
    }
    @Override
    public List<OrdreMission> getAllOrdreMission() {

        return omRepository.findAll();
    }

    @Override
    public Optional<OrdreMission> getOrdreMission(Long id) {
        return omRepository.findById(id);
    }

    @Override
    public OrdreMission updateOrdreMission(OrdreMission ordreMission, Long id) {
        ordreMission.setId(id);
        return omRepository.save(ordreMission);
    }

    @Override
    public void deleteOrdreMission(Long id) {
        omRepository.deleteById(id);
    }
    @Override
    public Page<OrdreMission> findPaginatedOrdresMissions(Pageable pageable, Login login) {
        List<OrdreMission> affaireList =new ArrayList<>();

        for ( Role role : login.getRoles()){
            if (role.getType().equals("admin") || (role.getType().equals("RH"))){
                affaireList=omRepository.findAllByOrderByIdDesc();
            }else {
               
                affaireList= omRepository.findAllByLoginOrderByIdDesc(login);
            }
        }
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<OrdreMission> listCons;

        if (affaireList.size() < startItem) {
            listCons = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, affaireList.size());
            listCons = affaireList.subList(startItem, toIndex);
        }
        Page<OrdreMission> consultPage = new PageImpl<OrdreMission>(listCons, PageRequest.of(currentPage, pageSize), affaireList.size());


        return consultPage;
    }
    
    @Override
    public void UpdateEtatImprime(OrdreMission ordreMission,Long id){
       ordreMission.setId(id);
        ordreMission.setImprime(true);
        omRepository.save(ordreMission);
    }
}
