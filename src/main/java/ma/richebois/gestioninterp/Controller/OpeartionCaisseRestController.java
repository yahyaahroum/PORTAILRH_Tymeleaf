package ma.richebois.gestioninterp.Controller;

import ma.richebois.gestioninterp.Enum.OperationState;
import ma.richebois.gestioninterp.Model.OperationCaisse;
import ma.richebois.gestioninterp.Repository.OperationCaisseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class OpeartionCaisseRestController {

    @Autowired
    private OperationCaisseRepository operationCaisseRepository;

    @GetMapping("/SearchOperation")
    public OperationCaisse getSearchResult(@RequestParam("id") String id){
        String newid = id.substring(8);
        Optional<OperationCaisse> operationCaisse = operationCaisseRepository.findById(Long.parseLong(newid));
        if (operationCaisse.isPresent()){
            return operationCaisse.get();
        }
           return null;
    }

    @PostMapping("/ValideOperation")
    public OperationCaisse valideOperation(@RequestParam("id") long id){
        Optional<OperationCaisse> operationCaisse = operationCaisseRepository.findById(id);
        operationCaisse.get().setEtat(OperationState.Validée.name());
        return operationCaisseRepository.save(operationCaisse.get());
    }


}
