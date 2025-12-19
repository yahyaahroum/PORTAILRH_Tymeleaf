package ma.richebois.gestioninterp.Service;

import ma.richebois.gestioninterp.Model.Affaire;
import ma.richebois.gestioninterp.Model.Pointage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.util.List;

public interface PointageService {

    public Page<Pointage> pointageList(Pageable pageable);
    public List<Pointage> pointages();
    public int countPointageByChantier();

    public void controlePointage(String dateStart,String dateEnd,String profile) throws ParseException;

}
