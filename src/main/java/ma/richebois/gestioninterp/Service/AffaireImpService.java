package ma.richebois.gestioninterp.Service;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Model.Affaire;
import ma.richebois.gestioninterp.Model.Login;
import ma.richebois.gestioninterp.Model.Role;
import ma.richebois.gestioninterp.Repository.AffaireRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@AllArgsConstructor
@Service
public class AffaireImpService implements AffaireService {

    private UserImpService userImpService;
    private AffaireRepository affaireRepository;

    @Override
    public List<Affaire> listChantierByRole(List<Role> roles) {
        Login login = userImpService.findbyusername();
        roles = login.getRoles();
        List<String> codechantier = new ArrayList<>();
        List<Affaire> chantierlist = login.getChantier();
        List<Affaire> chantierList1 = new ArrayList<>();
        for (Role role : roles){
            if (role.getType().equals("Pointeur") || role.getType().equals("ChefProjet")){
                for (Affaire chantier : chantierlist){
                    codechantier.add(chantier.getCode());
                    List<Affaire> listParChantier = affaireRepository.findAllByCodeInOrderByCodeAsc(codechantier);
                    chantierList1.addAll(listParChantier);
                }
                return chantierlist;
            }else 
            {
            	List<String> etapesAffaires= new ArrayList<>();
            	etapesAffaires.add("PRP");
                return affaireRepository.findByEtapeNotInOrderByCodeAsc(etapesAffaires);
            }
        }

        return null;
    }

    @Override
    public Page<Affaire> findPaginatedAffaire(Pageable pageable) {
        List<Affaire> affaireList = affaireRepository.findAll(Sort.by(Sort.Direction.ASC,"code"));
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Affaire> listCons;

        if (affaireList.size() < startItem) {
            listCons = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, affaireList.size());
            listCons = affaireList.subList(startItem, toIndex);
        }
        Page<Affaire> consultPage = new PageImpl<Affaire>(listCons, PageRequest.of(currentPage, pageSize), affaireList.size());


        return consultPage;
    }
}
