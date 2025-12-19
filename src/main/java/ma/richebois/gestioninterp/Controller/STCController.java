package ma.richebois.gestioninterp.Controller;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Enum.STCState;
import ma.richebois.gestioninterp.Model.*;
import ma.richebois.gestioninterp.Repository.AffaireRepository;
import ma.richebois.gestioninterp.Repository.IndividuRepository;
import ma.richebois.gestioninterp.Service.CanvasExportUtils;
import ma.richebois.gestioninterp.Service.STCImpService;
import ma.richebois.gestioninterp.Service.UserImpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
@AllArgsConstructor
@Controller
public class STCController {

    private STCImpService stcImpService;
    private IndividuRepository individuRepository;
    private AffaireRepository affaireRepository;
    private CanvasExportUtils canvasExportUtils;
    private UserImpService userImpService;


    @PostMapping("/STC/Ajouter/{id}")
    public String saveSTC(STC stc, @PathVariable("id") Long id, String chantier,Model model){

        Optional<Individu> individu = individuRepository.findById(id);
        Affaire affaire = affaireRepository.findByCode(chantier);


        stc.setIndividu(individu.get());
        stc.setAffaire(affaire);
        stc.setState(STCState.Saisie.name());

        STC stc1=stcImpService.addSTC(stc);
        if (stc1==null){
            model.addAttribute("errorSTC", "Cet STC est déjà ajouté pour le même employé :" + stc.getIndividu().getNom() + " " + stc.getIndividu().getPrenom() + " ,avec la même date");
            model.addAttribute("STCdemande",stc);
            return "Contrat/ListSTCDemande";
        }else
        userImpService.sendEmailCHPRSTC(stc);
        return "redirect:/STC/Demandes";
    }
    @GetMapping("/STC/ListeNoire")
    public String getAllSTC(Model model,@RequestParam("page") Optional<Integer> page,
                                @RequestParam("size") Optional<Integer> size){

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Page<STC> StcAll = stcImpService.findPaginatedAllSTC(PageRequest.of(currentPage - 1, pageSize),true);

        model.addAttribute("STCAll",StcAll);

        int totalPages = StcAll.getTotalPages();
        if (totalPages >0){
            int visiblePages = 6;
            int startPage = Math.max(1, currentPage - 3);
            int endPage = Math.min(totalPages, startPage + visiblePages - 1);
            if ((endPage - startPage + 1) < visiblePages) {
                startPage = Math.max(1, endPage - visiblePages + 1);
            }
            List<Integer> pageNumbers = IntStream.rangeClosed(startPage, endPage).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);

            model.addAttribute("pageNumbers", pageNumbers);
        }


        return"Contrat/ListeNoireStc";
    }
    @GetMapping("/STC/Demandes")
    public String getSTCDemande(Model model,@RequestParam("page") Optional<Integer> page,
                                @RequestParam("size") Optional<Integer> size){

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Page<STC> demande = stcImpService.findPaginatedSTC(PageRequest.of(currentPage - 1, pageSize),STCState.Saisie.name());

        model.addAttribute("STCdemande",demande);

        int totalPages = demande.getTotalPages();
        if (totalPages >0){
            int visiblePages = 6;
            int startPage = Math.max(1, currentPage - 3);
            int endPage = Math.min(totalPages, startPage + visiblePages - 1);
            if ((endPage - startPage + 1) < visiblePages) {
                startPage = Math.max(1, endPage - visiblePages + 1);
            }
            List<Integer> pageNumbers = IntStream.rangeClosed(startPage, endPage).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);

            model.addAttribute("pageNumbers", pageNumbers);
        }


        return"Contrat/ListSTCDemande";
    }
    @GetMapping("/STC/Valides")
    public String getSTCValide(Model model,@RequestParam("page") Optional<Integer> page,
                               @RequestParam("size") Optional<Integer> size){

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Page<STC> demande = stcImpService.findPaginatedSTC(PageRequest.of(currentPage - 1, pageSize),STCState.Validée.name());
        model.addAttribute("STCdemande",demande);

        int totalPages = demande.getTotalPages();
        if (totalPages >0){
            int visiblePages = 6;
            int startPage = Math.max(1, currentPage - 3);
            int endPage = Math.min(totalPages, startPage + visiblePages - 1);
            if ((endPage - startPage + 1) < visiblePages) {
                startPage = Math.max(1, endPage - visiblePages + 1);
            }
            List<Integer> pageNumbers = IntStream.rangeClosed(startPage, endPage).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);

            model.addAttribute("pageNumbers", pageNumbers);
        }
        return"Contrat/ListSTCValide";
    }

    @GetMapping("/STC/Approuves")
    public String getSTCApprouve(Model model,@RequestParam("page") Optional<Integer> page,
                                 @RequestParam("size") Optional<Integer> size){

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Page<STC> demande = stcImpService.findPaginatedSTC(PageRequest.of(currentPage - 1, pageSize),STCState.Approuvée.name());

        model.addAttribute("STCdemande",demande);

        int totalPages = demande.getTotalPages();
        if (totalPages >0){
            int visiblePages = 6;
            int startPage = Math.max(1, currentPage - 3);
            int endPage = Math.min(totalPages, startPage + visiblePages - 1);
            if ((endPage - startPage + 1) < visiblePages) {
                startPage = Math.max(1, endPage - visiblePages + 1);
            }
            List<Integer> pageNumbers = IntStream.rangeClosed(startPage, endPage).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);

            model.addAttribute("pageNumbers", pageNumbers);
        }

        return"Contrat/ListSTCApprouve";
    }


    @PostMapping("/STC/Validation/{id}")
    @PreAuthorize("hasAnyAuthority('admin','RH','ChefProjet')")
    public String valideSTC(@PathVariable("id") Long id){

        STC stc = stcImpService.validSTC(id);
        userImpService.sendEmailSTC(stc);

        return "redirect:/STC/Demandes";
    }

    @PostMapping("/STC/Approubation/{id}")
    @PreAuthorize("hasAnyAuthority('admin','RH')")
    public String approuverSTC(@PathVariable("id") Long id){

        stcImpService.ApprouverSTC(id);

        return "redirect:/STC/Valides";
    }

    @GetMapping("/STC/ExportNouveauSTC")
    @PreAuthorize("hasAnyAuthority('admin','RH')")
    public void exportToExcelSaisie( HttpServletResponse response,String start,String end) throws IOException, ParseException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Liste_Des_Nouveau_STC " + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        List<STC> stcList = stcImpService.getSTCByStates(start,end);
        canvasExportUtils.exportSTC(response,stcList);

    }

}
