package ma.richebois.gestioninterp.Service;
import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Model.Login;
import ma.richebois.gestioninterp.Model.Role;
import ma.richebois.gestioninterp.Model.Sanction;
import ma.richebois.gestioninterp.Repository.LoginRepository;
import ma.richebois.gestioninterp.Repository.SanctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
@AllArgsConstructor
@Service
public class SanctionServiceImp  implements SanctionService{
    private SanctionRepository sanctionRepository;
    private LoginRepository loginRepository;

    @Override
    public Sanction saveSanction(Sanction sanction) {
        return sanctionRepository.save(sanction);
    }

    @Override
    public Sanction updateSanction(Sanction sanction, Long id) {
        Sanction sanction2=sanctionRepository.findById(id).get();
        sanction.setId(id);
        sanction.setLogin(sanction2.getLogin());
        return sanctionRepository.save(sanction);
    }


    @Override
    public List<Sanction> getAllSanction() {
        return sanctionRepository.findAll();
    }

    @Override
    public Optional<Sanction> findByIdSanction(Long id) {
        return sanctionRepository.findById(id);
    }

    @Override
    public void deleteSanction(Long id) {
        sanctionRepository.deleteById(id);
    }
    @Override
    public Page<Sanction> findPaginatedSanctions(Pageable pageable , Long Id ,String typeSanction) {
        List<Sanction> affaireList=new ArrayList<>();
        Login login=loginRepository.findById(Id).get();
       for ( Role role : login.getRoles()){
            if (role.getType().equals("admin") || (role.getType().equals("RH"))){
                affaireList=sanctionRepository.findAllBytypeSanctionOrderByIdDesc(typeSanction);
            }else {
               
                affaireList= sanctionRepository.getSanctionsbyLogin_Type(Id,typeSanction);
            }
        }
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Sanction> listCons;

        if (affaireList.size() < startItem) {
            listCons = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, affaireList.size());
            listCons = affaireList.subList(startItem, toIndex);
        }
        Page<Sanction> consultPage = new PageImpl<Sanction>(listCons, PageRequest.of(currentPage, pageSize), affaireList.size());


        return consultPage;
    }
}
