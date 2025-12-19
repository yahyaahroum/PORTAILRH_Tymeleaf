package ma.richebois.gestioninterp.Service;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Enum.IndividuState;
import ma.richebois.gestioninterp.Model.*;
import ma.richebois.gestioninterp.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@AllArgsConstructor
@Service
public class FichierServiceImp implements FichierService {

    private AjoutRepository ajoutRepository;
    private ErrorMessageRepository errorMessageRepository;
    private ImportRepository importRepository;
    private IndividuRepository individuRepository;
    private FonctionRepository fonctionRepository;
    private AffaireRepository affaireRepository;


    @Override
    public List<?> storeCanevas(MultipartFile file, Long id) throws IOException {

        List<?> result = null;
        Optional<Import> imp = importRepository.findById(id);
        List<ErrorMessage> errorMessageList = null;
        result = CanvasImportUtils.parseExcelFile(file.getInputStream(), imp.get());
        if (result.get(0) instanceof Ajout) {
            List<Ajout> personList = (List<Ajout>) result;
            List<?> persVerifResult = personVerification(personList);
            if (persVerifResult instanceof Ajout) {
                for (Ajout p : (List<Ajout>) persVerifResult) {
                    p.setState(IndividuState.Validé.name());
                    p.setLibelle(fonctionRepository.findByCodefonction(p.getFonction()).getLibelle());
                    p.setDesignation(affaireRepository.findByCode(p.getCodechantier()).getDesignation());
                    ajoutRepository.save(p);
                }
            } else {
                errorMessageList = (List<ErrorMessage>) persVerifResult;
                errorMessageRepository.saveAll(errorMessageList);
                result = errorMessageList;
            }

        } else if (result.get(0) instanceof ErrorMessage) {
            errorMessageList = (List<ErrorMessage>) result;
            errorMessageRepository.saveAll(errorMessageList);
        }

        return result;
    }

    @Override
    public List<?> personVerification(List<Ajout> listperson) {
        List<Ajout> person = new ArrayList<Ajout>();
        List<ErrorMessage> errorIndiv = new ArrayList<ErrorMessage>();
        for (Ajout p : listperson) {
            List<Individu> ListInd = individuRepository.findAllByCodecinOrderByIndividuDesc(p.getCodecin());
            if (ListInd.size() > 0) {
                if (p.getMatricule() != ListInd.get(0).getIndividu()) {
                    ErrorMessage errormsg = new ErrorMessage();
                    errormsg.setMessage("Veuillez vérifier l'individu avec le code CIN: " + p.getCodecin() + " , il est enregistré avec le matricule " + ListInd.get(0).getIndividu() + " sur DIVALTO");
                    errorIndiv.add(errormsg);
                } else
                    person.add(p);

            } else
                person.add(p);
        }
        if (errorIndiv.size() > 0) {
            return (List<?>) errorIndiv;
        } else
            return (List<?>) person;
    }

}
