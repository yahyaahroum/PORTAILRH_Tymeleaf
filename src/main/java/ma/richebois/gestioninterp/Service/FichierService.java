package ma.richebois.gestioninterp.Service;

import ma.richebois.gestioninterp.Model.Ajout;
import ma.richebois.gestioninterp.Model.Fichiers;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FichierService {

    public List<?> storeCanevas(MultipartFile file,Long id) throws IOException;
    public List<?> personVerification(List<Ajout> listperson);
}
