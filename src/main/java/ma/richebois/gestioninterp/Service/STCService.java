package ma.richebois.gestioninterp.Service;

import ma.richebois.gestioninterp.Model.Inspecteur;
import ma.richebois.gestioninterp.Model.STC;
import org.springframework.data.domain.*;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

public interface STCService {

    public STC addSTC(STC stc);
    public List<STC> getSTCListByStateAndRole(String state);
    public STC validSTC(Long id);
    public STC ApprouverSTC(Long id);
    public Page<STC> findPaginatedSTC(Pageable pageable,String state);
    public Page<STC> findPaginatedAllSTC(Pageable pageable,Boolean etat);

    public List<STC> getSTCByStates(String start,String end) throws ParseException;




}
