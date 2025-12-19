package ma.richebois.gestioninterp.Controller;


import ma.richebois.gestioninterp.Config.GlobalVariableConfig;
import ma.richebois.gestioninterp.DTO.AjoutSearchDTO;
import ma.richebois.gestioninterp.Enum.IndividuState;
import ma.richebois.gestioninterp.Model.*;

import ma.richebois.gestioninterp.Repository.*;

import ma.richebois.gestioninterp.Service.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@AllArgsConstructor
public class PersonController {
	
    private FichierServiceImp fichierServiceImp;   
    private AjoutSearchRepository ajoutSearchRepository;   
    private AjoutServiceImp personServiceImp;   
    private CanvasExportUtils canvasExportUtils;    
    private ImportServiceImp importServiceImp;    
    private FonctionRepository fonctionRepository;    
    private IndividuRepository individuRepository;   
    private UserImpService userImpService;   
    private AffaireImpService affaireImpService;   
    private RIBRepository ribRepository;   
    private ContratRepository contratRepository;    
    private VilleRepository villeRepository;   
    private BanqueRepository banqueRepository;   
    private AjoutRepository ajoutRepository;
    private GlobalVariableConfig globalVariableConfig;
    @GetMapping("/")
    @PreAuthorize("hasAnyAuthority('admin','RH','Pointeur')")
    public String home(String codecin, Model model) {
    	DonneesATransfererALaVue(model,codecin);
        return "Contrat/NouveauContratForm";
    }

    @GetMapping("/Personne/ListePersonnesImportee")
    @PreAuthorize("hasAnyAuthority('admin','RH')")
    public ModelAndView getListPerson(ModelMap model, @ModelAttribute("listperson") List<Ajout> flashAttribute, String codecin) {

        String done = "le fichier était importé avec succés";
        model.addAttribute("listperson", flashAttribute);
        model.addAttribute("success", done);
        model.addAttribute("importId", flashAttribute.get(0).getImp().getId());
        return new ModelAndView("ListPersonImport", model);
    }


    @GetMapping("/Ajout/Importer")
    @PreAuthorize("hasAnyAuthority('admin','RH')")
    public String importer() {
        return "Personne/ListPersonImport";
    }

    @GetMapping("/Personne/ListeErreurs")
    @PreAuthorize("hasAnyAuthority('admin','RH')")
    public ModelAndView getListErrors(ModelMap model, @ModelAttribute("errorMessages") Object flashAttribute, String codecin) {

        String echec = "le fichier n'a pas été importé, vérifiez les erreurs";
        model.addAttribute("errorImport", echec);
        model.addAttribute("errorMessages", flashAttribute);
        return new ModelAndView("ListError", model);
    }


//    @GetMapping("/Personne/ListePersonnes")
//    @PreAuthorize("hasAnyAuthority('admin','RH')")
//    public String getAllPersonnes(Model model, @RequestParam("page") Optional<Integer> page,
//                                  @RequestParam("size") Optional<Integer> size) {
//
//        int currentPage = page.orElse(1);
//        int pageSize = size.orElse(40);
//
//        Page<Ajout> personnes = personServiceImp.findPaginatedAjout(PageRequest.of(currentPage - 1, pageSize));
//
//        model.addAttribute("personnes", personnes);
//
//        int totalPages = personnes.getTotalPages();
//        if (totalPages > 0) {
//            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
//                    .boxed()
//                    .collect(Collectors.toList());
//            model.addAttribute("pageNumbers", pageNumbers);
//        }
//
//        return "ListPersonnes";
//    }

    @PostMapping("/Personnes/Importer")
    @PreAuthorize("hasAnyAuthority('admin','RH')")
    public String uploadCanvas(@RequestParam("file") MultipartFile file, Import imp, Model model, RedirectAttributes attribute) throws IOException {
        Import impor = new Import();

        impor = importServiceImp.saveImport(imp);

        List<?> result = fichierServiceImp.storeCanevas(file, impor.getId());
        if (result.get(0) instanceof Ajout) {
            List<?> result1 = fichierServiceImp.personVerification((List<Ajout>) result);
            if (result1.get(0) instanceof Ajout) {
                String done = "le fichier était importé avec succés";
                model.addAttribute("done", done);
                attribute.addFlashAttribute("listperson", (List<Ajout>) result1);
                return "redirect:/Personne/ListePersonnesImportee";
            } else if (result1.get(0) instanceof Individu) {
                attribute.addFlashAttribute("errorMessages", (List<ErrorMessage>) result1);
                return "redirect:/Personne/ListeErreurs";
            }
        } else if (result.get(0) instanceof Object) {
            String echec = "le fichier n'était pas importé, verifiez les erreurs";
            attribute.addFlashAttribute("echec", echec);
            attribute.addFlashAttribute("errorMessages", (List<ErrorMessage>) result);
            return "redirect:/Personne/ListeErreurs";
        } else
            return "redirect:/Personne/ListeErreurs";

        return null;
    }

    @GetMapping("/Personnes/export/{id}")
    @PreAuthorize("hasAnyAuthority('admin','RH')")
    public void exportToExcel(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Liste_Des_Personnes" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        List<Ajout> personList = personServiceImp.listImport(id);
        canvasExportUtils.export(response, personList);
    }

    @GetMapping("/canevasImport")
    @PreAuthorize("hasAnyAuthority('admin','RH')")
    public void exportCanevas(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Canevas d'import.xlsx";
        response.setHeader(headerKey, headerValue);

        canvasExportUtils.exportCanvas(response);

    }


    @GetMapping("/Recherche")
    @PreAuthorize("hasAnyAuthority('admin','RH','Pointeur')")
    public String getPersonneSearch(String key, Model model) {
        Login login = userImpService.findbyusername();
        model.addAttribute("key", key);
        boolean numtel = false;

        if (Character.digit(key.charAt(0), 10) > 0) {
            Individu individu = individuRepository.findAllByIndividuOrderByIndividuDesc(Integer.parseInt(key));
            if (individu!=null){
                List<Ajout> ajoutList = ajoutRepository.findAllByMatriculeOrderByIdDesc(individu.getIndividu());
                if (individu.getTele()==null){
                	model.addAttribute("numTelBoolean", CheckNumTel(ajoutList));
                }
                if (individu.getTele()!=null){
                    numtel = true;
                    model.addAttribute("numTelBoolean", numtel);
                }
                model.addAttribute("numTelBoolean", numtel);
                model.addAttribute("listperson", individu);
            }
            if (individu==null){
                String errorSearch = "Aucune personne associée à ce code  : " + key.toUpperCase();
                model.addAttribute("errorSearch", errorSearch);
            }
            model.addAttribute("chantier", affaireImpService.listChantierByRole(login.getRoles()));
            model.addAttribute("villes",villeRepository.findAll(Sort.by(Sort.Direction.ASC,"designation")));
            return "Personne/SearchPersonne";
        } else if (Character.isLetter(key.charAt(0))) {
            List<Individu> listind = individuRepository.findAllByCodecinOrderByIndividuDesc(key);
            if (!listind.isEmpty()){
                List<Ajout> ajoutList = ajoutRepository.findAllByMatriculeOrderByIdDesc(listind.get(0).getIndividu());
                if (listind.get(0).getTele()==null){
                	model.addAttribute("numTelBoolean", CheckNumTel(ajoutList));
                }
                if (listind.get(0).getTele()!=null){
                    numtel = true;
                    model.addAttribute("numTelBoolean", numtel);
                }
                model.addAttribute("listperson", listind.get(0));
            }
            if (listind.isEmpty()){
                String errorSearch = "Aucune personne associée à ce code  : " + key.toUpperCase();
                model.addAttribute("errorSearch", errorSearch);
            }

            model.addAttribute("chantier", affaireImpService.listChantierByRole(login.getRoles()));
            model.addAttribute("villes",villeRepository.findAll(Sort.by(Sort.Direction.ASC,"designation")));
            return "Personne/SearchPersonne";
        }
        String errorSearch = "Aucune personne associée à ce code  : " + key.toUpperCase();
        model.addAttribute("errorSearch", errorSearch);
        return "Personne/SearchPersonne";


    }

    @PostMapping("/Ajout/Formulaire")
    @PreAuthorize("hasAnyAuthority('admin','RH','Pointeur')")
    public String saveForm(@RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2,
                           @RequestParam("file3") MultipartFile file3, @RequestParam("file4") MultipartFile file4,
                           @RequestParam("file6") MultipartFile file6,
                           @RequestParam("file5") MultipartFile file5, Ajout ajout, String pessai, String rib, Model model) throws IOException {

        Login login = userImpService.findbyusername();

        if (rib.length() > 24 || rib.length() < 24) {
            model.addAttribute("error", "Le rib contient : " + rib.length() + " nombre. Veuillez vérifier le rib");
            DonneesATransfererALaVue(model, "");
            return "Contrat/NouveauContratForm";
            
        } else if (rib.length() == 24) {
            personServiceImp.savePerson(file1, file2, file3, file4, file5, file6, ajout, pessai);
            userImpService.sendEmail(ajout);
            return "redirect:/";
        }
        return null;
    }

    @GetMapping("/Personne/ListePersonnes")
    @PreAuthorize("hasAnyAuthority('admin','RH','Pointeur')")
    public String getPersonnesAvalider(Model model, @RequestParam("page") Optional<Integer> page,
                                       @RequestParam("size") Optional<Integer> size,@RequestParam("etat") String state) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Page<Ajout> personnes = personServiceImp.findPaginatedAjoutByState(PageRequest.of(currentPage - 1, pageSize), state);

        model.addAttribute("personnes", personnes);
        model.addAttribute("etat", state);

        int totalPages = personnes.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }


        return "Personne/ListPersonnes";
    }

    @PostMapping("/Ajout/Personnes/valider/{id}")
    @PreAuthorize("hasAnyAuthority('admin','RH')")
    public String validerPersonnes(@PathVariable("id") long id, String salaire, String pessai) {
        personServiceImp.valider(id, salaire,pessai);
        return "redirect:/Personne/ListePersonnes?etat=A_Valider";
    }

    @PostMapping("/Ajout/Personnes/Rejeter/{id}")
    @PreAuthorize("hasAnyAuthority('admin','RH')")
    public String rejeterPersonnes(@PathVariable("id") long id, String motifRejet) {
        personServiceImp.rejeter(id, motifRejet);
        return "redirect:/Personne/ListePersonnes?etat=A_Valider";
    }


    @GetMapping("/Contrat/ListeAImprimer")
    @PreAuthorize("hasAnyAuthority('admin','RH','Pointeur')")
    public String getContratAImprimer(Model model, @RequestParam("page") Optional<Integer> page,
                                      @RequestParam("size") Optional<Integer> size) {


        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Page<Ajout> personnes = personServiceImp.findPaginatedAjoutByState(PageRequest.of(currentPage - 1, pageSize), IndividuState.A_Imprimer.name());

        model.addAttribute("personnes", personnes);

        int totalPages = personnes.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "Contrat/ListContratAImprimer";
    }

    @GetMapping("/Contrat/ListeImprimee")
    @PreAuthorize("hasAnyAuthority('admin','RH','Pointeur')")
    public String getContratImprimee(Model model, @RequestParam("page") Optional<Integer> page,
                                     @RequestParam("size") Optional<Integer> size) {


        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Page<Ajout> personnes = personServiceImp.findPaginatedAjoutByState(PageRequest.of(currentPage - 1, pageSize), IndividuState.Imprimé.name());

        model.addAttribute("personnes", personnes);

        int totalPages = personnes.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "Contrat/ListContratImprimee";
    }

    @GetMapping("/Personnes/Editer/{id}")
    @PreAuthorize("hasAnyAuthority('admin','RH','Pointeur')")
    public String getPersonneUpdatePage(Model model, @PathVariable("id") Long id) {
        Login login = userImpService.findbyusername();

        List<Affaire> chantier = affaireImpService.listChantierByRole(login.getRoles());
        List<Fonction> fonction = fonctionRepository.findAll();

        Ajout pers = personServiceImp.findById(id);


        model.addAttribute("personne", pers);
        model.addAttribute("chantier", chantier);
        model.addAttribute("fonctions", fonction);
        model.addAttribute("banques", banqueRepository.findAll(Sort.by(Sort.Direction.ASC,"designation")));

        return "Personne/EditPersonnee";

    }

    @PostMapping("/Personnes/Editer/{id}")
    @PreAuthorize("hasAnyAuthority('admin','RH','Pointeur')")
    public String updatePersonne(@PathVariable("id") Long id, Model model, Ajout ajout, @RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2,
                                 @RequestParam("file3") MultipartFile file3, @RequestParam("file4") MultipartFile file4,
                                 @RequestParam("file5") MultipartFile file5, @RequestParam("file6") MultipartFile file6, String rib) throws IOException {
        Login login = userImpService.findbyusername();
        List<Ajout> personnes = personServiceImp.getAll();
        List<Affaire> chantier = affaireImpService.listChantierByRole(login.getRoles());
        List<Fonction> fonction = fonctionRepository.findAll();

        Ajout pers = null;
        for (Ajout p : personnes) {
            if (p.getId() == id) {
                pers = p;
            }
        }
        if (rib.length() > 24 || rib.length() < 24) {
            model.addAttribute("errorRib", "Le rib contient : " + rib.length() + " nombre. Veuillez vérifier le rib");

            model.addAttribute("personne", pers);
            model.addAttribute("chantier", chantier);
            model.addAttribute("fonction", fonction);
            return "EditPersonnee";

        } else if (rib.length() == 24) {
            model.addAttribute("personne", pers);
            model.addAttribute("chantier", chantier);
            model.addAttribute("fonction", fonction);
            personServiceImp.updatePerson(file1, file2, file3, file4, file5, file6, ajout);
            return "redirect:/Personne/ListePersonnes?etat=A_Valider";
        }
        return null;

    }

    @GetMapping("/Personnes/Supprimer/{id}")
    @PreAuthorize("hasAnyAuthority('admin','RH')")
    public String deletePerson(@PathVariable("id") Long id) {
        personServiceImp.deletePerson(id);
        return "redirect:/Personne/ListePersonnes?etat=A_Valider";
    }

    @PostMapping("/Ajout/Contrat/Depot/{id}")
    @PreAuthorize("hasAnyAuthority('admin','RH','Pointeur')")
    public String contratDepot(@PathVariable("id") Long id, @RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2,
                               @RequestParam("file3") MultipartFile file3, Optional<Ajout> ajout) throws IOException {
        personServiceImp.personneContractUpload(id, file1, file2, file3, ajout);
        return "redirect:/Personne/ListePersonnes?etat=Validé";
    }

    @GetMapping("/fichier/{id}/{fileName}")
    @ResponseBody
    public void showFiles(@PathVariable("fileName") String fileName, @PathVariable("id") Long id, HttpServletResponse response) {
        Ajout person = personServiceImp.findById(id);
        if (fileName.indexOf(".doc") > -1) response.setContentType("application/msword");
        if (fileName.indexOf(".docx") > -1) response.setContentType("application/msword");
        if (fileName.indexOf(".pdf") > -1) response.setContentType("application/pdf");

        response.setHeader("Content-Disposition", "attachment; filename" + fileName);
        response.setHeader("Content-Transfer-Encoding", "binary");

        try {
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            FileInputStream fis = new FileInputStream(globalVariableConfig.getGlobalVariable()+ person.getNom().replaceAll("\\s", "") + person.getPrenom().replaceAll("\\s", "") + "/" + fileName);
            System.out.println(fis);
            int len;
            byte[] buf = new byte[99999];
            while ((len = fis.read(buf)) > 0) {
                bos.write(buf, 0, len);
            }
            bos.close();
            response.flushBuffer();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @GetMapping("/Ajout/Personnes/Fichiers/{id}")
    public String listFiles(@PathVariable("id") Long id, Model model) {

        Ajout person = personServiceImp.findById(id);

       // File folder = new File(globalVariableConfig.getGlobalVariable()+ person.getNom().replaceAll("\\s", "") + person.getPrenom().replaceAll("\\s", ""));
        File folder = new File(globalVariableConfig.getGlobalVariable()+ person.getNom().replaceAll("\\s", "") + person.getPrenom().replaceAll("\\s", ""));
        System.out.println("fichier: "+folder);

        File[] listOfFiles = folder.listFiles();

        System.out.println(Arrays.toString(listOfFiles));
       // System.out.println(listOfFiles);
       // Arrays.sort(listOfFiles, Comparator.comparingLong(File::lastModified).reversed());
        model.addAttribute("files", listOfFiles);
        model.addAttribute("person", person);

        return "ListFile";
    }

    @GetMapping("/Personnes/ExportNouveauContrat")
    @PreAuthorize("hasAnyAuthority('admin','RH')")
    public void exportToExcelSaisie(HttpServletResponse response,String start,String end) throws IOException, ParseException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Liste_Des_Nouveau_Contrats" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        List<Ajout> personList = personServiceImp.getPersonnesSaisie(start,end);

        canvasExportUtils.export(response, personList);
    }

    @GetMapping("/Personne/NouveauContrat/{id}")
    public String getIndividuNouveauContrat(Model model, @PathVariable("id") Long id) {

        Individu individu = personServiceImp.getIndividu(id);
        Login login = userImpService.findbyusername();

        List<RIB> rib = ribRepository.findAllByIndividuOrderByCodeRibDesc(individu.getIndividu());
        List<Contrat> contrat = contratRepository.findAllByMatriculeOrderByNumcontratDesc(individu.getIndividu());

        if (rib.size() != 0) {
            model.addAttribute("individuRib", rib.get(0));
        }
        model.addAttribute("contratInd", contrat.get(0));
        model.addAttribute("chantier", affaireImpService.listChantierByRole(login.getRoles()));
        model.addAttribute("fonction", fonctionRepository.findAll());
        model.addAttribute("individu", individu);
        model.addAttribute("banques", banqueRepository.findAll(Sort.by(Sort.Direction.ASC,"designation")));

        return "Contrat/NouveauContrat";
    }

    @PostMapping("/Ajout/NouveauContrat")
    @PreAuthorize("hasAnyAuthority('admin','RH','Pointeur')")
    public String saveNewContrat(@RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2,
                                 @RequestParam("file3") MultipartFile file3, @RequestParam("file4") MultipartFile file4,
                                 @RequestParam("file6") MultipartFile file6,
                                 @RequestParam("file5") MultipartFile file5, Ajout ajout, String pessai, String date, String rib, Model model) throws IOException, ParseException {
        Login login = userImpService.findbyusername();
        if (rib.length() > 24 || rib.length() < 24) {
            model.addAttribute("errorRib", "Le rib contient : " + rib.length() + " nombre. Veuillez vérifier le rib");

            model.addAttribute("chantier", affaireImpService.listChantierByRole(login.getRoles()));
            model.addAttribute("fonction", fonctionRepository.findAll());
            return "Contrat/NouveauContratForm";
        } else if (rib.length() == 24) {



            DateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
            ajout.setDatenaissance(sourceFormat.parse(date));
            personServiceImp.savePerson(file1, file2, file3, file4, file5, file6, ajout, pessai);
            userImpService.sendEmail(ajout);
            return "redirect:/";
        }
        return null;

    }

    @GetMapping("/Personnel/Couverture/{id}")
    @PreAuthorize("hasAnyAuthority('admin','RH')")
    public ResponseEntity<byte[]> generateCouverture(@PathVariable("id") Long id) throws IOException, JRException {

            Ajout p = personServiceImp.findById(id);
            Resource resource = new ClassPathResource("File/FichePersonnelle.jrxml");
            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(Collections.singleton(p));
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(resource.getURL().getPath()));

            HashMap<String, Object> map = new HashMap<>();
            JasperPrint report = JasperFillManager.fillReport(compileReport, map, beanCollectionDataSource);

            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=Couverture"+p.getNom().trim().toUpperCase()+"_"+p.getPrenom().trim().toUpperCase()+".pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }
    @GetMapping("/Personne/AdvancedSearch")
    @PreAuthorize("hasAnyAuthority('admin','RH','Pointeur')")
    public String SearchForAjout(Model model, AjoutSearchDTO ajout,@RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size)
    {
    	int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        List<Ajout> personnes = ajoutSearchRepository.searchAjout(ajout);
        Page<Ajout> ajoutPage = personServiceImp.AjoutPage(PageRequest.of(currentPage - 1, pageSize),personnes);
        model.addAttribute("personnes", ajoutPage);
        int totalPages = ajoutPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);


        }
        model.addAttribute("search","search");
        model.addAttribute("personneDTO",ajout);
        return "Personne/ListPersonnes";
    }
    private boolean CheckNumTel(List<Ajout> ajoutList) 
    {
    	boolean numtel=false;
    	if (!ajoutList.isEmpty()){
    		
        	if (ajoutList.get(0).getNumtele().length()>=10){
                numtel = true;    
            }
        }
    	return numtel;
    }
    private Model DonneesATransfererALaVue(Model model,String codecin) 
    {
    	Login login = userImpService.findbyusername();
    	model.addAttribute("codecin", codecin);
        model.addAttribute("chantier", affaireImpService.listChantierByRole(login.getRoles()));
        model.addAttribute("fonction", fonctionRepository.findAll(Sort.by(Sort.Direction.ASC,"libelle")));
        model.addAttribute("banques", banqueRepository.findAll(Sort.by(Sort.Direction.ASC,"designation")));
        return model;
    }
}



