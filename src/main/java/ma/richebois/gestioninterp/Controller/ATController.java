package ma.richebois.gestioninterp.Controller;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Config.GlobalVariableConfig;
import ma.richebois.gestioninterp.Model.AT;
import ma.richebois.gestioninterp.Model.Ajout;
import ma.richebois.gestioninterp.Model.Certificat;
import ma.richebois.gestioninterp.Model.Login;
import ma.richebois.gestioninterp.Repository.ATRepository;
import ma.richebois.gestioninterp.Repository.VilleRepository;
import ma.richebois.gestioninterp.Service.ATService;
import ma.richebois.gestioninterp.Service.AffaireImpService;
import ma.richebois.gestioninterp.Service.CertificatService;
import ma.richebois.gestioninterp.Service.UserImpService;
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
@AllArgsConstructor
@Controller
public class ATController {
    private ATService atService;
    private CertificatService certificatService;
    private ATRepository atRepository;
    private UserImpService userImpService;
    private AffaireImpService affaireImpService;
    private VilleRepository villeRepository;
    private GlobalVariableConfig globalVariableConfig;

    @GetMapping("/AT/Tous")
    public String getAllAT(Model model, @RequestParam("page") Optional<Integer> page,
                           @RequestParam("size") Optional<Integer> size){

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(40);

        Page<AT> ats = atService.findPaginatedAT(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("ats",ats);

        Login login = userImpService.findbyusername();
        model.addAttribute("chantier", affaireImpService.listChantierByRole(login.getRoles()));
        model.addAttribute("villes",villeRepository.findAll(Sort.by(Sort.Direction.ASC,"designation")));

        int totalPages = ats.getTotalPages();
        if (totalPages >0){
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "AT/ListAT";

    }

    @PostMapping("/AT/Ajouter/{id}")
    public String saveAT(AT at,@PathVariable("id") long id){
        atService.saveAT(at,id);
        return "redirect:/AT/Tous";
    }

    @PostMapping("/AT/Modifier/{id}")
    public String updateAT(AT at,@PathVariable("id") long id){
        atService.updateAT(at,id);
        return "redirect:/AT/Tous";
    }

    @GetMapping("/AT/Supprimer/{id}")
    public String deleteAT(@PathVariable("id") long id){
        atService.deleteAT(id);
        return "redirect:/AT/Tous";
    }

    @PostMapping("/AT/Certificat/Ajouter/{at}")
    public String saveCertif(@PathVariable("at") long at,@RequestPart("certificat") Certificat certificat, @RequestPart("file") MultipartFile file) throws IOException {
        certificatService.saveCertif(certificat,at,file);
        return "redirect:/AT/Tous";
    }

    @GetMapping("/AT/Certificats/{at}")
    public String listFiles(@PathVariable("at") Long at,Model model) {
        Optional<AT> accident = atRepository.findById(at);
        List<Certificat> certificats=certificatService.getCertifByAT(at);
        model.addAttribute("person",accident.get().getIndividu());
        model.addAttribute("at",accident.get());

        model.addAttribute("files", certificats);


        return "AT/ListCertif";
    }

    @GetMapping("/Certificat/{at}/{fileName}")
    @ResponseBody
    public void showFiles(@PathVariable("fileName") String fileName, @PathVariable("at") Long at, HttpServletResponse response) {
        Optional<AT> accident = atRepository.findById(at);
        String directory = accident.get().getIndividu().getNom().replaceAll("\\s", "") + accident.get().getIndividu().getPrenom().replaceAll("\\s", "");
        //String rootCertif = "/home/PortailRH-BETA-DOC/" + directory+"/AT";
        String rootCertif = globalVariableConfig.getGlobalVariable() + directory+"/AT";
        if (fileName.indexOf(".doc") > -1) response.setContentType("application/msword");
        if (fileName.indexOf(".docx") > -1) response.setContentType("application/msword");
        if (fileName.indexOf(".pdf") > -1) response.setContentType("application/pdf");

        response.setHeader("Content-Disposition", "attachment; filename" + fileName);
        response.setHeader("Content-Transfer-Encoding", "binary");

        try {
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            FileInputStream fis = new FileInputStream(rootCertif+ "/" + fileName);
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
}
