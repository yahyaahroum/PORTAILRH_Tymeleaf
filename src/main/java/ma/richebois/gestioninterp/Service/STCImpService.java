package ma.richebois.gestioninterp.Service;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Enum.DechargeState;
import ma.richebois.gestioninterp.Enum.STCState;
import ma.richebois.gestioninterp.Model.*;
import ma.richebois.gestioninterp.Repository.DechargeRepository;
import ma.richebois.gestioninterp.Repository.STCRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
@AllArgsConstructor
@Service
public class STCImpService implements STCService {

    private STCRepository stcRepository;
    private UserImpService userImpService;
    private DechargeRepository dechargeRepository;

    @Override
    public STC addSTC(STC stc) {
        Integer stcCountVerif = stcRepository.countByIndividuAndAffaireAndDatedepart(stc.getIndividu(),stc.getAffaire(),stc.getDatedepart());
        if (stcCountVerif==0){
            return stcRepository.save(stc);
        }else
            return null;
    }


    @Override
    public List<STC> getSTCListByStateAndRole(String state) {
        Login login = userImpService.findbyusername();
        List<Role> roles= login.getRoles();
        List<String> states = new ArrayList<>();
        states.add(state);
        List<Affaire> chantierlist = login.getChantier();
        for (Role role : roles) {
            if (role.getType().equals("Pointeur") || role.getType().equals("ChefProjet") ) {
                List<STC> getSTCbyRoleAndChantierwithState = stcRepository.findAllByStateInAndAffaireInOrderByDatedepartAsc(states,chantierlist);
                return getSTCbyRoleAndChantierwithState;
            }else

                return stcRepository.findAllByStateInOrderByDatedepartAsc(states);
        }
        return null;
    }

    @Override
    public STC validSTC(Long id) {

        Optional<STC> stc = stcRepository.findById(id);
        stc.get().setState(STCState.Validée.name());

        return stcRepository.save(stc.get());
    }

    @Override
    public STC ApprouverSTC(Long id) {
        Optional<STC> stc = stcRepository.findById(id);
        stc.get().setState(STCState.Approuvée.name());
        return stcRepository.save(stc.get());
        
    }

    @Override
    public Page<STC> findPaginatedSTC(Pageable pageable,String state) {

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<STC> listCons;

        if (getSTCListByStateAndRole(state).size() < startItem) {
            listCons = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, getSTCListByStateAndRole(state).size());
            listCons = getSTCListByStateAndRole(state).subList(startItem, toIndex);
        }
        Page<STC> consultPage = new PageImpl<STC>(listCons, PageRequest.of(currentPage, pageSize), getSTCListByStateAndRole(state).size());


        return consultPage;

    }

    @Override
    public Page<STC> findPaginatedAllSTC(Pageable pageable,Boolean etat) {
        List<STC> stcList = stcRepository.findAllBylistNoir(etat);
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<STC> listCons = null;
        if (stcList.size() < startItem) {
            listCons = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, stcList.size());
            listCons = stcList.subList(startItem, toIndex);
        }
        Page<STC> consultPage = new PageImpl<STC>(listCons, PageRequest.of(currentPage, pageSize), stcList.size());


        return consultPage;

    }

    @Override
    public List<STC> getSTCByStates(String start,String end) throws ParseException {
        List<String> states = new ArrayList<>();
        states.add(STCState.Approuvée.name());
        Date datestart = new SimpleDateFormat("yyyy-MM-dd").parse(start);
        Date dateend = new SimpleDateFormat("yyyy-MM-dd").parse(end);
        List<STC> stcExport = stcRepository.findAllByStateInAndDatedepartBetweenOrderByDatedepartAsc(states,datestart,dateend);

        return stcExport;
    }


}
