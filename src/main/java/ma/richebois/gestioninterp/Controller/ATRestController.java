package ma.richebois.gestioninterp.Controller;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Enum.CertifType;
import ma.richebois.gestioninterp.Model.AT;
import ma.richebois.gestioninterp.Model.Ajout;
import ma.richebois.gestioninterp.Model.Certificat;
import ma.richebois.gestioninterp.Repository.ATRepository;
import ma.richebois.gestioninterp.Service.CertificatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@AllArgsConstructor
@RestController
public class ATRestController {

    private CertificatService certificatService;
    private ATRepository atRepository;

    @GetMapping(value = "/Certificat/All")
    public List<Certificat> getAllByAT(@RequestParam("at") long at){
        return certificatService.getCertifByAT(at);
    }
    @GetMapping(value = "/AT")
    public AT getOneAT(@RequestParam("at") long at){
        return atRepository.findById(at).get();
    }

    @GetMapping(value = "/VerifCertificat")
    public List<String> verifByATAndType(@RequestParam("at") long at){
        Integer countVerif = certificatService.countCertifByType(at, CertifType.Initial.name());
        List<String> certifType = new ArrayList<>();
        if (countVerif==0){
            certifType.add(CertifType.Initial.name());
            certifType.add(CertifType.Prolongation.name());
            certifType.add(CertifType.Reprise_de_travail.name());
            certifType.add(CertifType.Guerison.name());
        }
        if (countVerif!=0){
            certifType.add(CertifType.Prolongation.name());
            certifType.add(CertifType.Reprise_de_travail.name());
            certifType.add(CertifType.Guerison.name());
        }
        return certifType;
    }
    @PostMapping(value = "/AT/Certificat/Ajouter/{at}" , produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveCertif(HttpServletResponse response, @PathVariable("at") long at, @RequestPart("certificat") Certificat certificat, @RequestPart("file") MultipartFile file) throws IOException {
        System.out.println("accident ::: "+at);
        certificatService.saveCertif(certificat,at,file);
    }



}
