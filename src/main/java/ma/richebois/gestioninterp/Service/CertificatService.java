package ma.richebois.gestioninterp.Service;

import ma.richebois.gestioninterp.Model.Certificat;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CertificatService {

    public List<Certificat> getCertifByAT(long at);
    public Integer countCertifByType(long at,String type);

    public void saveCertif(Certificat certificat, long at, MultipartFile file) throws IOException;
}
