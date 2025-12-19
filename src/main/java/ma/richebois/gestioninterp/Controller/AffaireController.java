package ma.richebois.gestioninterp.Controller;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Model.Affaire;
import ma.richebois.gestioninterp.Repository.AffaireRepository;
import ma.richebois.gestioninterp.Repository.VilleRepository;
import ma.richebois.gestioninterp.Service.AffaireImpService;
import ma.richebois.gestioninterp.Utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
@AllArgsConstructor
@Controller
public class AffaireController {
    private AffaireImpService affaireImpService;
    private VilleRepository villeRepository;
    private AffaireRepository affaireRepository;


    @GetMapping("/Projets/tous")
    private String getAllProjets(Model model,@RequestParam("page") Optional<Integer> page,
                                 @RequestParam("size") Optional<Integer> size){

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(40);

        Page<Affaire> chantiers = affaireImpService.findPaginatedAffaire(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("chantiers",chantiers);
        model.addAttribute("villes",villeRepository.findAll(Sort.by(Sort.Direction.ASC,"designation")));

        int totalPages = chantiers.getTotalPages();
        if (totalPages >0) {
            // ✅ pagination compacte
            List<Integer> pageNumbers = PaginationUtils.getCompactPageNumbers(currentPage, chantiers.getTotalPages(), 6);
            model.addAttribute("pageNumbers", pageNumbers);

        }
            return "ListAffaire";
    }

    @PostMapping("/Affaire/Modifier/{id}")
    public String editAffaire(@PathVariable("id") long id,Affaire affaire){
        Optional<Affaire> affaire1 = affaireRepository.findById(id);
        affaire1.get().setNewDesign(affaire.getNewDesign());
        affaire1.get().setVille(affaire.getVille());
        affaire1.get().setAdresse(affaire.getAdresse());
        affaireRepository.save(affaire1.get());

        return "redirect:/Projets/tous";
    }
}