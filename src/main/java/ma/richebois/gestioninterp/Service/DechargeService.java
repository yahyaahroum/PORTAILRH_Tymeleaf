package ma.richebois.gestioninterp.Service;

import ma.richebois.gestioninterp.Model.Decharge;
import ma.richebois.gestioninterp.DTO.DechargeDto;
import ma.richebois.gestioninterp.Model.Materiel;
import net.sf.jasperreports.engine.JRException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DechargeService {

    public List<Decharge> getAllDecharge(String statut);
    public Decharge saveDecharge(DechargeDto decharge) ;
    public List<Materiel> getAllMaterial();
    public ResponseEntity<byte[]> generateDecharge(long id) throws IOException, JRException;
    public Decharge annuler(long id);
    public Decharge signer(long id,MultipartFile file) throws IOException;
    public Decharge liberer(long id);
    public Page<Decharge> dechargeList(Pageable pageable,String statut);

}
