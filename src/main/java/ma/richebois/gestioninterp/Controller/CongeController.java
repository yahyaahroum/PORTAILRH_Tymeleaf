package ma.richebois.gestioninterp.Controller;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Enum.CongeState;
import ma.richebois.gestioninterp.Enum.STCState;
import ma.richebois.gestioninterp.Model.*;
import ma.richebois.gestioninterp.Repository.AffaireRepository;
import ma.richebois.gestioninterp.Repository.IndividuRepository;
import ma.richebois.gestioninterp.Repository.RoleRepository;
import ma.richebois.gestioninterp.Service.CongeService;
import ma.richebois.gestioninterp.Service.UserImpService;
import net.sf.jasperreports.engine.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
@AllArgsConstructor
@Controller
public class CongeController {
    private CongeService congeService;
    private IndividuRepository individuRepository;
    private AffaireRepository affaireRepository;
    private UserImpService userImpService;
    private RoleRepository roleRepository;


    @PostMapping("/Conge/Ajouter/{id}")
    public String saveConge(Conge conge, @PathVariable("id") Long id, String chantier, Model model,String numTele){

        Optional<Individu> individu = individuRepository.findById(id);
        Affaire affaire = affaireRepository.findByCode(chantier);
        individu.get().setTele(numTele);

        conge.setIndividu(individu.get());
        conge.setAffaire(affaire);
        conge.setEtat(STCState.Saisie.name());

        Conge conge1 = congeService.save(conge);
        individuRepository.save(individu.get());

        if (conge1==null){
            model.addAttribute("errorconge", "Ce congé est déjà ajouté pour le même employé : " + conge.getIndividu().getNom() + " " + conge.getIndividu().getPrenom() + " ,avec la même date debut et date fin");
            model.addAttribute("STCdemande",conge);
            return "Conge/ListCongeDemande";
        }else

        return "redirect:/Conge/Demandes";
    }

    @PostMapping("/Conge/Depot/{id}")
    @PreAuthorize("hasAnyAuthority('admin','RH','Pointeur')")
    public String depotConge(@PathVariable("id") Long id, @RequestParam("file") MultipartFile file) throws IOException {

        congeService.saveDemande(id,file);
        congeService.sendEmailConge(id);

        return "redirect:/Conge/Demandes";
    }

    @GetMapping("/Conge/Demandes")
    public String getSTCDemande(Model model,@RequestParam("page") Optional<Integer> page,
                                @RequestParam("size") Optional<Integer> size){

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Page<Conge> demande =congeService.findPaginatedConge(PageRequest.of(currentPage - 1, pageSize), CongeState.Saisie.name());

        model.addAttribute("congeDemande",demande);

        int totalPages = demande.getTotalPages();
        if (totalPages >0){
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }


        return"Conge/ListCongeDemande";
    }
    @GetMapping("/Conge/Valides")
    public String getCongeValide(Model model,@RequestParam("page") Optional<Integer> page,
                               @RequestParam("size") Optional<Integer> size){

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Page<Conge> congeValid =congeService.findPaginatedConge(PageRequest.of(currentPage - 1, pageSize),CongeState.Validée.name());

        model.addAttribute("congeValid",congeValid);

        int totalPages = congeValid.getTotalPages();
        if (totalPages >0){
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "Conge/ListCongeValide";
    }

    @GetMapping("/Conge/Importe")
    public String getCongeImporte(Model model,@RequestParam("page") Optional<Integer> page,
                                 @RequestParam("size") Optional<Integer> size){

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        Login login = userImpService.findbyusername();
        Role pointeur = roleRepository.findByType("Pointeur");

        Page<Conge> congeValid =congeService.findPaginatedConge(PageRequest.of(currentPage - 1, pageSize),CongeState.Importée.name());

        model.addAttribute("congeValid",congeValid);
        model.addAttribute("login",login);
        model.addAttribute("pointeur",pointeur);

        int totalPages = congeValid.getTotalPages();
        if (totalPages >0){
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "Conge/ListCongeImporte";
    }

    @GetMapping("/Conge/Refuse")
    @PreAuthorize("hasAnyAuthority('admin','RH','Pointeur')")
    public String getCongeRefuse(Model model,@RequestParam("page") Optional<Integer> page,
                                 @RequestParam("size") Optional<Integer> size){

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Page<Conge> congeValid =congeService.findPaginatedConge(PageRequest.of(currentPage - 1, pageSize),CongeState.Refusée.name());

        model.addAttribute("congeValid",congeValid);

        int totalPages = congeValid.getTotalPages();
        if (totalPages >0){
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "Conge/ListCongeRefuse";
    }


    @PostMapping("/Conge/Validation/{id}")
    @PreAuthorize("hasAnyAuthority('admin','RH')")
    public String valideConge(@PathVariable("id") Long id){

        congeService.valideConge(id);

        return "redirect:/Conge/Importe";
    }

    @PostMapping("/Conge/Refus/{id}")
    @PreAuthorize("hasAnyAuthority('admin','RH')")
    public String RefusConge(@PathVariable("id") Long id, String motifRefus){

        congeService.refusConge(id,motifRefus);

        return "redirect:/Conge/Importe";
    }

    @GetMapping("/Conge/Supprimer/{id}")
    @PreAuthorize("hasAnyAuthority('admin','RH','Pointeur')")
    public String deleteConge(@PathVariable("id") Long id){
        congeService.deleteConge(id);
        return "redirect:/Conge/Demandes";
    }

    @GetMapping("/Conge/DemandeConge/{id}")
    @PreAuthorize("hasAnyAuthority('admin','RH','Pointeur')")
    public ResponseEntity<byte[]> generateCouverture(@PathVariable("id") Long id) throws IOException, JRException {
        return congeService.generateDemandeConge(id);
    }

    @GetMapping("/Conge/fichier/{id}")
    @ResponseBody
    public void showFiles(@PathVariable("id") Long id, HttpServletResponse response) {
        congeService.showFiles(id,response);
    }

 }
