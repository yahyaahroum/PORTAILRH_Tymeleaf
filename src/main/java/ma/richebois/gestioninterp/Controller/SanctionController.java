package ma.richebois.gestioninterp.Controller;
import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Enum.SanctionState;
import ma.richebois.gestioninterp.Model.Login;
import ma.richebois.gestioninterp.Model.Role;
import ma.richebois.gestioninterp.Model.Sanction;
import ma.richebois.gestioninterp.Repository.IndividuRepository;
import ma.richebois.gestioninterp.Repository.SanctionRepository;
import ma.richebois.gestioninterp.Service.SanctionService;
import ma.richebois.gestioninterp.Service.UserImpService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
@AllArgsConstructor
@Controller
@PreAuthorize("hasAnyAuthority('admin','RH','Pointeur')")
public class SanctionController {
    private SanctionService sanctionService;
    private SanctionRepository sanctionRepository;
    private IndividuRepository individuRepository;
    private UserImpService loginImpService;
    private Login loginConnectet(){
   
    return loginImpService.findbyusername();
    }

    @GetMapping("/Avertissements/Tous")
    public String getAvertissements(Model model, @RequestParam("page") Optional<Integer> page,
                                     @RequestParam("size") Optional<Integer> size){
        String role_login;
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(40);

        Page<Sanction> avertissements = sanctionService.findPaginatedSanctions(PageRequest.of(currentPage - 1, pageSize),loginConnectet().getId(),SanctionState.Avertissement.name());
        model.addAttribute("sanctions",avertissements);
      for ( Role role : loginConnectet().getRoles()){
           role_login=role.getType().trim();
                model.addAttribute("role", role_login);
        }
   
        model.addAttribute("emps",individuRepository.getContratActif());
        Sanction avr=new Sanction();
        model.addAttribute("avr",avr);

        int totalPages = avertissements.getTotalPages();
         currentPage = avertissements.getNumber() + 1;
        int maxPagesToShow = 6;

        int startPage = Math.max(1, currentPage - 3);
        int endPage = Math.min(totalPages, startPage + maxPagesToShow - 1);

        if ((endPage - startPage + 1) < maxPagesToShow) {
            startPage = Math.max(1, endPage - maxPagesToShow + 1);
        }

        List<Integer> pageNumbers = IntStream.rangeClosed(startPage, endPage).boxed().collect(Collectors.toList());
        model.addAttribute("pageNumbers", pageNumbers);

        Login login = loginImpService.findbyusername();

        model.addAttribute("login",login);
        return "Sanction/ListeAvertissements";

    }

    @PostMapping("/Avertissement/new")
    public String saveAvertissement(@ModelAttribute("sanction") Sanction sanction){
        sanction.setLogin(loginConnectet());
        sanctionService.saveSanction(sanction);
        return "redirect:/Avertissements/Tous";
    }

    @GetMapping("/Avertissement/Editer/{id}")
    public String getdetailAvertissement(Model model, @PathVariable("id") Long id){
      
         model.addAttribute("emps",individuRepository.getContratActif());
        model.addAttribute("Avertissement",sanctionRepository.findById(id).get());
        return "Sanction/EditAvertissement";
    }
    @PostMapping("/Avertissements/Modifier/{id}")
    public String updateAvertissement(Sanction sanction, @PathVariable("id") long id){

      for ( Role role : loginConnectet().getRoles()){
            if (role.getType().equals("admin") || (role.getType().equals("RH"))){
                 if(!sanction.getValid() || (sanction.getValid() && !sanction.getImprime())){
                    sanctionService.updateSanction(sanction,id);
                 }
            }else if (role.getType().equals("Pointeur")) {
                if(!sanction.getValid()){
                   sanctionService.updateSanction(sanction,id);
                }
            }
        }
      
        return "redirect:/Avertissements/Tous";
    }

    @GetMapping("/Avertissements/Supprimer/{id}")
    public String deleteAvertissement(@PathVariable("id") long id){
         Sanction sanction= sanctionService.findByIdSanction(id).get();
        for ( Role role : loginConnectet().getRoles()){
            if (role.getType().equals("admin") || (role.getType().equals("RH"))){
                 if(!sanction.getValid() || (sanction.getValid() && !sanction.getImprime())){
                     sanctionService.deleteSanction(id);
                 }
            }else if (role.getType().equals("Pointeur")) {
                if(!sanction.getValid()){
                     sanctionService.deleteSanction(id);               
                }
            }
        }


        return "redirect:/Avertissements/Tous";
    }
    @PostMapping("/Avertissements/validation/{id}")
    public String UpdateEtatValidationAVR(@PathVariable("id") Long id){
        Sanction sanction= sanctionService.findByIdSanction(id).get();
      sanction.setValide(true);
      for ( Role role : loginConnectet().getRoles()){
            if (role.getType().equals("admin") || (role.getType().equals("RH"))){
                 if(!sanction.getValid() || (sanction.getValid() && !sanction.getImprime())){
                    sanctionService.updateSanction(sanction,id);
                 }
            
            }
        }

      
      
       return "redirect:/Avertissements/Tous";
  
    }



    //Mise à Pied
    @GetMapping("/MiseaPied/Tous")
    public String getMiseaPied(Model model, @RequestParam("page") Optional<Integer> page,
                               @RequestParam("size") Optional<Integer> size){

        String role_login;
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(40);
        Page<Sanction> mise_a_pied = sanctionService.findPaginatedSanctions(PageRequest.of(currentPage - 1, pageSize),loginConnectet().getId(),SanctionState.Mise_a_pied.name());
        model.addAttribute("mise_a_pied",mise_a_pied);
        model.addAttribute("emps",individuRepository.getContratActif());
        for ( Role role : loginConnectet().getRoles()){
           role_login=role.getType().trim();
                model.addAttribute("role", role_login);
        }
   
        Sanction Mise_AP=new Sanction();
        model.addAttribute("Mise_AP",Mise_AP);

        int totalPages = mise_a_pied.getTotalPages();
        if (totalPages >0){
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        Login login = loginImpService.findbyusername();

        model.addAttribute("login",login);

        return "Sanction/ListeMiseaPied";

    }
    @PostMapping("/MiseaPied/new")
    public String saveSanction(@ModelAttribute("sanction") Sanction ordreMission){
       ordreMission.setTypeSanction("Mise_a_pied");
        ordreMission.setLogin(loginConnectet());
        sanctionService.saveSanction(ordreMission);
        return "redirect:/MiseaPied/Tous";
    }
      @GetMapping("/MiseaPied/Editer/{id}")
    public String getdetailMiseaPied(Model model, @PathVariable("id") Long id){
      
         model.addAttribute("emps",individuRepository.getContratActif());
        model.addAttribute("m_a_p",sanctionRepository.findById(id).get());
        return "Sanction/EditMiseaPied";
    }
    @PostMapping("/MiseaPied/Modifier/{id}")
    public String updateMiseaPied(Sanction sanction, @PathVariable("id") long id){
         for ( Role role : loginConnectet().getRoles()){
            if (role.getType().equals("admin") || (role.getType().equals("RH"))){
                 if(!sanction.getValid() || (sanction.getValid() && !sanction.getImprime())){
                sanctionService.updateSanction(sanction,id);
                 }
            }else if (role.getType().equals("Pointeur")) {
                if(!sanction.getValid()){
                  sanctionService.updateSanction(sanction,id);
                }
            }
        }

       
        return "redirect:/MiseaPied/Tous";
    }

    @GetMapping("/MiseaPied/Supprimer/{id}")
    
    public String deleteMiseaPied(@PathVariable("id") long id){
         Sanction sanction= sanctionService.findByIdSanction(id).get();
        for ( Role role : loginConnectet().getRoles()){
            if (role.getType().equals("admin") || (role.getType().equals("RH"))){
                 if(!sanction.getValid() || (sanction.getValid() && !sanction.getImprime())){
                     sanctionService.deleteSanction(id);
                 }
            }else if (role.getType().equals("Pointeur")) {
                if(!sanction.getValid()){
                    sanctionService.deleteSanction(id);
                }
            }
        }
        return "redirect:/MiseaPied/Tous";    
    }
    @PostMapping("/MiseaPied/ValidateMA/{id}")
    public String UpdateEtatVAlidationMA(@PathVariable("id") Long id){
      Sanction sanction= sanctionService.findByIdSanction(id).get();
      sanction.setValide(true);
      for ( Role role : loginConnectet().getRoles()){
            if (role.getType().equals("admin") || (role.getType().equals("RH"))){
                 if(!sanction.getValid() || (sanction.getValid() && !sanction.getImprime())){
                    sanctionService.updateSanction(sanction, id);
                 }
            }
        }

   
        return "redirect:/MiseaPied/Tous";  
    }


    @GetMapping(value = "/MiseaPied/pdf/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> MiseAPiedPDF(@PathVariable Long id) throws JRException, IOException {

        Sanction sanction= sanctionService.findByIdSanction(id).get();
         sanction.setImprime(true);
        sanctionService.updateSanction(sanction, id);
        JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(Collections.singleton(sanction), false);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("Nom", sanction.getIndividu().getNom().trim());

        parameters.put("Prenom", sanction.getIndividu().getPrenom().trim());
        parameters.put("Adresse", sanction.getIndividu().getAdresserue().trim()+" " + sanction.getIndividu().getAdresseville().trim()+" "+sanction.getIndividu().getAdressepays());
        String sexe=sanction.getIndividu().getSexe().trim();
        if(sexe.equals("2"))
        {
            parameters.put("Sexe", "Madame");
        }else{
            parameters.put("Sexe", "Monsieur");
        }
                Date dateFinSanction=sanction.getDateApplication();
                Long duree=sanction.getDuree();
                Date dateReprise;

        Calendar c = Calendar.getInstance();
        c.setTime(dateFinSanction);
        c.add(Calendar.DATE, Math.toIntExact(duree)-1);
        dateFinSanction = c.getTime();
        parameters.put("DateFinSanction",dateFinSanction);
        c.setTime(dateFinSanction);
        c.add(Calendar.DATE,1);
        dateReprise = c.getTime();
        parameters.put("DateReprise",dateReprise);
        Resource resource = new ClassPathResource("File/MiseAPied.jrxml");
        JasperReport compileReport = JasperCompileManager
                .compileReport(new FileInputStream(resource.getURL().getPath()));

        JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, parameters,beanCollectionDataSource);


        byte data[] = JasperExportManager.exportReportToPdf(jasperPrint);


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=MiseAPied.pdf");

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
    }


    @GetMapping(value = "/Avertissement/pdf/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> AvertissementPDF(@PathVariable Long id) throws JRException, IOException {

        Sanction sanction= sanctionService.findByIdSanction(id).get();
        sanction.setImprime(true);
        sanctionService.updateSanction(sanction, id);
        JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(Collections.singleton(sanction), false);
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("Nom", sanction.getIndividu().getNom().trim());

        parameters.put("Prenom", sanction.getIndividu().getPrenom().trim());
        parameters.put("Adresse", sanction.getIndividu().getAdresserue().trim()+" " + sanction.getIndividu().getAdresseville().trim()+" "+sanction.getIndividu().getAdressepays());
        String sexe=sanction.getIndividu().getSexe().trim();
    if(sexe.equals("2"))
        {
            parameters.put("Sexe", "Madame");
        }else{
            parameters.put("Sexe", "Monsieur");
        }
        Resource resource = new ClassPathResource("File/LettreAvertissement.jrxml");
        JasperReport compileReport = JasperCompileManager
                .compileReport(new FileInputStream(resource.getURL().getPath()));

        JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, parameters,beanCollectionDataSource);


        byte data[] = JasperExportManager.exportReportToPdf(jasperPrint);

        System.err.println(data);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=LettreAvertissement.pdf");

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
    }

}


