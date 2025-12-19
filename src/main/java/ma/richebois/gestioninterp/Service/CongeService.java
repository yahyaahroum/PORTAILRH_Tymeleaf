package ma.richebois.gestioninterp.Service;

import ma.richebois.gestioninterp.Model.Conge;
import ma.richebois.gestioninterp.Model.STC;
import net.sf.jasperreports.engine.JRException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface CongeService {

    public Conge save(Conge conge);
    public Conge saveDemande(long id, MultipartFile file) throws IOException;
    public Conge valideConge(Long id);

    public Conge refusConge(Long id, String motifRefus);

    public boolean deleteConge(Long id);
    public List<Conge> getListCongeByStateAndRole(String state);
    public Page<Conge> findPaginatedConge(Pageable pageable, String state);
    public ResponseEntity<byte[]> generateDemandeConge(Long id) throws IOException, JRException;

    public void showFiles(Long id, HttpServletResponse response);

    public void sendEmailConge(Long id);
}
