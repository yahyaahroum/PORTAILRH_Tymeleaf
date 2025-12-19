package ma.richebois.gestioninterp.Service;

import ma.richebois.gestioninterp.Model.Inspecteur;
import ma.richebois.gestioninterp.Repository.InspecteurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class InspecteurServiceImp implements InspecteurService{
@Autowired
public InspecteurRepository inspecteurRepo;
    @Override
    public Inspecteur saveInspecteur(Inspecteur insp) {
        return inspecteurRepo.save(insp);
    }

    @Override
    public Inspecteur updateInspecteur(Inspecteur insp, Long id) {
        insp.setId(id);
        return inspecteurRepo.save(insp);
    }

    @Override
    public List<Inspecteur> getAllInspecteur() {
        return inspecteurRepo.findAll();
    }

    @Override
    public Optional<Inspecteur> findByIdInspecteur(Long id) {
        return inspecteurRepo.findById(id);
    }

    @Override
    public void deleteInspecteur(Long id) {
        inspecteurRepo.deleteById(id);
    }
    @Override
    public Page<Inspecteur> findPaginatedInspecteurs(Pageable pageable) {
        List<Inspecteur> affaireList = inspecteurRepo.findAll(Sort.by(Sort.Direction.ASC,"id"));
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Inspecteur> listCons;

        if (affaireList.size() < startItem) {
            listCons = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, affaireList.size());
            listCons = affaireList.subList(startItem, toIndex);
        }
        Page<Inspecteur> consultPage = new PageImpl<Inspecteur>(listCons, PageRequest.of(currentPage, pageSize), affaireList.size());


        return consultPage;
    }
}
