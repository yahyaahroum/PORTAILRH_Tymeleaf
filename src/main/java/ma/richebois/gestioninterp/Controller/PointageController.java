package ma.richebois.gestioninterp.Controller;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Model.Login;
import ma.richebois.gestioninterp.Model.Pointage;
import ma.richebois.gestioninterp.Repository.IndividuRepository;
import ma.richebois.gestioninterp.Repository.PointageRepository;
import ma.richebois.gestioninterp.Service.AffaireService;
import ma.richebois.gestioninterp.Service.PointageImpService;
import ma.richebois.gestioninterp.Service.UserImpService;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
@AllArgsConstructor
@Controller
public class PointageController {
    private IndividuRepository individuRepository;
    private UserImpService userImpService;
    private AffaireService affaireService;
    private PointageImpService pointageImpService;
    private PointageRepository pointageRepository;
    @GetMapping("/Pointage")
    public String getPointagePage(Model model, @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size){
        Login login = userImpService.findbyusername();
        model.addAttribute("chantier", affaireService.listChantierByRole(login.getRoles()));
        model.addAttribute("emps",individuRepository.getContratActif());
        
        model.addAttribute("pointages",pointageImpService.pointageList(PageRequest.of(page.orElse(1) - 1, size.orElse(10))));
        return "Pointage/ListPointage";
    }

    @GetMapping("/Pointage/FilterPage")
    public String getPointageFilterPage(Model model){
        Login login = userImpService.findbyusername();
        model.addAttribute("chantier", affaireService.listChantierByRole(login.getRoles()));
        model.addAttribute("emps",individuRepository.getContratActif());
        return "Pointage/ListPointageFilter";
    }
    
    @GetMapping(value = "/Pointage/delete/{id}")
    public boolean deletePointage (@PathVariable("id") Long id){
        Optional<Pointage> pointage = pointageRepository.findById(id);
        pointageRepository.delete(pointage.get());
        return true;
    }
}
