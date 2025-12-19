package ma.richebois.gestioninterp.Controller;

import ma.richebois.gestioninterp.Model.*;
import ma.richebois.gestioninterp.Repository.ATRepository;
import ma.richebois.gestioninterp.Repository.VilleRepository;
import ma.richebois.gestioninterp.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class InspecteurController {

   @Autowired
   private InspecteurService inspecteurService;

    @GetMapping("/Inspecteurs/tous")
    public String getAllAInspecteurs(Model model, @RequestParam("page") Optional<Integer> page,
                           @RequestParam("size") Optional<Integer> size){

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(40);

        Page<Inspecteur> inspecteurs = inspecteurService.findPaginatedInspecteurs(PageRequest.of(currentPage - 1, pageSize));
        model.addAttribute("ats",inspecteurs);
        int totalPages = inspecteurs.getTotalPages();
        if (totalPages >0){
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "ListInspecteurs";

    }
    @GetMapping("/ajouterP")
    public String afficherParId(Model model) {
        Inspecteur per=new Inspecteur();
        model.addAttribute("inspecteur",per);
        return "NouveauInspecteur";
    }

    @PostMapping("/Inspecteur/new")
    public String saveInspecteur(@ModelAttribute("inspecteur") Inspecteur inspecteur){
        inspecteurService.saveInspecteur(inspecteur);
        return "redirect:/Inspecteurs/tous";
    }

    @PostMapping("/Inspecteur/Modifier/{id}")
    public String updateAT(Inspecteur inspecteur,@PathVariable("id") long id){
        inspecteurService.updateInspecteur(inspecteur,id);
        return "redirect:/Inspecteurs/tous";
    }

    @GetMapping("/Inspecteur/Supprimer/{id}")
    public String deleteAT(@PathVariable("id") long id){
        inspecteurService.deleteInspecteur(id);
        return "redirect:/Inspecteurs/tous";
    }


}
