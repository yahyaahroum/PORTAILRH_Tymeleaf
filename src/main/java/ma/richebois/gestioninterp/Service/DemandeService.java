package ma.richebois.gestioninterp.Service;

import ma.richebois.gestioninterp.Model.Conge;
import ma.richebois.gestioninterp.Model.Demande;
import ma.richebois.gestioninterp.Model.STC;
import ma.richebois.gestioninterp.Model.TypeDemande;
import net.sf.jasperreports.engine.JRException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface DemandeService {

    public Demande saveDemande(Demande demande,String numTele);
    public Page<Demande> getAllByEmpPeagable(Pageable pageable);
    public List<Demande> getAllByEmp();
    public List<TypeDemande> getAllType();

    public boolean deleteDemande(Long id);

    public Demande valider(Long id);

    public Demande refuser(Long id,String motifRefus);

    public List<Demande> getAllByEmpAndState(String state);

    public Page<Demande> findByEmpAndState(Pageable pageable,String state);

    public ResponseEntity<byte[]> generateDemande(Long id) throws JRException, IOException;

    public void sendEmailDemande(Demande demande);



}
