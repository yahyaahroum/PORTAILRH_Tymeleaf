package ma.richebois.gestioninterp.Controller;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Model.Decharge;
import ma.richebois.gestioninterp.Model.Login;
import ma.richebois.gestioninterp.Repository.IndividuRepository;
import ma.richebois.gestioninterp.Repository.MaterielRepository;
import ma.richebois.gestioninterp.Service.AffaireService;
import ma.richebois.gestioninterp.Service.DechargeService;
import ma.richebois.gestioninterp.Service.UserImpService;
import ma.richebois.gestioninterp.Utils.PaginationUtils;
import net.sf.jasperreports.engine.JRException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AllArgsConstructor
@Controller
@PreAuthorize("hasAnyAuthority('admin','RH','Tech')")
public class DechargeController {
    private IndividuRepository individuRepository;
    private DechargeService dechargeService;
    private AffaireService affaireService;
    private MaterielRepository materielRepository;
    private UserImpService userImpService;

    @GetMapping("/Decharge/Liste")
    public String getPointagePage(Model model,
                                  @RequestParam("page") Optional<Integer> page,
                                  @RequestParam("statut") Optional<String> statut,
                                  @RequestParam("size") Optional<Integer> size,
                                  @RequestParam("search") Optional<String> search) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(30);
        String statutdecharge = statut.orElse("All");
        String searchTerm = search.orElse("");

        // Appel de la méthode du service avec le paramètre de recherche
        Page<Decharge> listDecharge;
        if (searchTerm.isEmpty()) {
            // Utilise la méthode originale si pas de recherche
            listDecharge = dechargeService.dechargeList(PageRequest.of(currentPage - 1, pageSize), statutdecharge);
        } else {
            // Utilise la nouvelle méthode avec recherche
            // Note: Il faut que votre service DechargeService implémente cette méthode
            // ou faire un cast vers DechargeImpService si nécessaire
            if (dechargeService instanceof ma.richebois.gestioninterp.Service.DechargeImpService) {
                ma.richebois.gestioninterp.Service.DechargeImpService impService =
                        (ma.richebois.gestioninterp.Service.DechargeImpService) dechargeService;
                listDecharge = impService.dechargeList(PageRequest.of(currentPage - 1, pageSize), statutdecharge, searchTerm);
            } else {
                // Fallback sur la méthode originale
                listDecharge = dechargeService.dechargeList(PageRequest.of(currentPage - 1, pageSize), statutdecharge);
            }
        }

        model.addAttribute("dechargeList", listDecharge);
        model.addAttribute("search", searchTerm);

        // pagination compacte
        List<Integer> pageNumbers = PaginationUtils.getCompactPageNumbers(currentPage, listDecharge.getTotalPages(), 6);
        model.addAttribute("pageNumbers", pageNumbers);

        // autres attributs
        Login login = userImpService.findbyusername();
        model.addAttribute("statut", statutdecharge);
        model.addAttribute("chantiers", affaireService.listChantierByRole(login.getRoles()));
        model.addAttribute("emps", individuRepository.getContratActif());
        model.addAttribute("materielList", materielRepository.findAll(Sort.by(Sort.Direction.ASC, "Type")));

        return "Decharge/ListDecharge";
    }

    @GetMapping("/Decharge/generer/{id}")
    public ResponseEntity<byte[]> genererDecharge(@PathVariable("id") long id) throws JRException, IOException {


      return dechargeService.generateDecharge(id);
    }

    @GetMapping("/Decharge/Annuler/{id}")
    public String Annuler(@PathVariable("id") long id,
                          @RequestParam(value = "statut", defaultValue = "All") String statut,
                          @RequestParam(value = "search", defaultValue = "") String search) {
        dechargeService.annuler(id);

        // Redirection avec conservation des paramètres de recherche
        StringBuilder redirectUrl = new StringBuilder("redirect:/Decharge/Liste?statut=").append(statut);
        if (!search.isEmpty()) {
            redirectUrl.append("&search=").append(search);
        }

        return redirectUrl.toString();
    }

    @PostMapping("/Decharge/Signer/{id}")
    public String signer(@PathVariable("id") long id,
                         @RequestParam("file") MultipartFile file,
                         @RequestParam(value = "statut", defaultValue = "All") String statut,
                         @RequestParam(value = "search", defaultValue = "") String search) throws IOException {
        dechargeService.signer(id, file);

        // Redirection avec conservation des paramètres de recherche
        StringBuilder redirectUrl = new StringBuilder("redirect:/Decharge/Liste?statut=").append(statut);
        if (!search.isEmpty()) {
            redirectUrl.append("&search=").append(search);
        }

        return redirectUrl.toString();
    }

    @GetMapping("/Decharge/liberer/{id}")
    public String liberer(@PathVariable("id") long id,
                          @RequestParam(value = "statut", defaultValue = "All") String statut,
                          @RequestParam(value = "search", defaultValue = "") String search) {
        dechargeService.liberer(id);

        // Redirection avec conservation des paramètres de recherche
        StringBuilder redirectUrl = new StringBuilder("redirect:/Decharge/Liste?statut=").append(statut);
        if (!search.isEmpty()) {
            redirectUrl.append("&search=").append(search);
        }

        return redirectUrl.toString();
    }
}