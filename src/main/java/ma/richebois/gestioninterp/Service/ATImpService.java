package ma.richebois.gestioninterp.Service;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Model.*;
import ma.richebois.gestioninterp.Repository.ATRepository;
import ma.richebois.gestioninterp.Repository.IndividuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
@AllArgsConstructor
@Service
public class ATImpService implements ATService{

    private ATRepository atRepository;
    private IndividuRepository individuRepository;
    private UserImpService userImpService;

    @Override
    public AT saveAT(AT at,long id) {

        Optional<Individu> individu = individuRepository.findById(id);
        at.setIndividu(individu.get());
        return atRepository.save(at);
    }

    @Override
    public AT updateAT(AT at,long id) {
        Optional<AT> a = atRepository.findById(id);
        a.get().setAdresseAT(at.getAdresseAT());
        a.get().setChantierAT(at.getChantierAT());
        a.get().setDateAT(at.getDateAT());
        a.get().setPlaceAT(at.getPlaceAT());
        a.get().setCauseAT(at.getCauseAT());
        a.get().setArretTravail(at.isArretTravail());
        a.get().setTypeBlessure(at.getTypeBlessure());
        a.get().setVilleAT(at.getVilleAT());
        a.get().setJoursArT(at.getJoursArT());
        a.get().setDateDebutArT(at.getDateDebutArT());
        a.get().setDateFinArT(at.getDateFinArT());

        return atRepository.save(a.get());
    }


    @Override
    public List<AT> listAT() {
        Login login = userImpService.findbyusername();
        List<Role> roles= login.getRoles();
        List<Affaire> chantierlist = login.getChantier();
        for (Role role : roles) {
            if (role.getType().equals("Pointeur") || role.getType().equals("ChefProjet") ) {
                List<AT> atList = atRepository.findAllByChantierATInOrderByIdDesc(chantierlist);
                return atList;
            }else
                return atRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        }
        return null;
    }

    @Override
    public Page<AT> findPaginatedAT(Pageable pageable) {

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<AT> listCons;

        if (listAT().size() < startItem) {
            listCons = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, listAT().size());
            listCons = listAT().subList(startItem, toIndex);
        }
        Page<AT> consultPage = new PageImpl<AT>(listCons, PageRequest.of(currentPage, pageSize), listAT().size());


        return consultPage;
    }

    @Override
    public void deleteAT(long id) {
        atRepository.deleteById(id);
    }
}
