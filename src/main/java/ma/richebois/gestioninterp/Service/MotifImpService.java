package ma.richebois.gestioninterp.Service;

import ma.richebois.gestioninterp.Model.Motif;
import ma.richebois.gestioninterp.Repository.MotifRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MotifImpService implements MotifService {

    @Autowired
    private MotifRepository motifRepository;

    @Override
    public List<Motif> getAllMotif() {
        return motifRepository.findAll();
    }
}
