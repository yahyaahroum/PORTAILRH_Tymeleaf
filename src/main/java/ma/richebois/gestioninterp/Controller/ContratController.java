package ma.richebois.gestioninterp.Controller;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Config.GlobalVariableConfig;
import ma.richebois.gestioninterp.Model.Affaire;
import ma.richebois.gestioninterp.Model.Ajout;

import ma.richebois.gestioninterp.Enum.IndividuState;
import ma.richebois.gestioninterp.Repository.AffaireRepository;
import ma.richebois.gestioninterp.Repository.AjoutRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
@AllArgsConstructor
@Controller
public class ContratController {
    private AjoutRepository ajoutRepository;
    private AffaireRepository affaireRepository;
    private GlobalVariableConfig globalVariableConfig;

    @GetMapping("/Contrat/CDD/{id}")
    @PreAuthorize("hasAnyAuthority('admin','RH','Pointeur')")
    public ResponseEntity<byte[]> generateContrat(@PathVariable("id") Long id) throws IOException, JRException {
        Ajout p = ajoutRepository.getPersonForContract(id);
        System.out.println(p);
        Affaire affaire = affaireRepository.findByCode(p.getCodechantier());
            if (affaire.getNewDesign() == null){

                return null;
            }else {
                String directory = p.getNom().replaceAll("\\s", "") + p.getPrenom().replaceAll("\\s", "");
                Path root = Paths.get(globalVariableConfig.getGlobalVariable() + directory);
                if (Files.notExists(root)){
                    Files.createDirectory(root);
                }

            p.setDesignation(affaire.getNewDesign());
                p.setState(IndividuState.A_Imprimer.name());
                ajoutRepository.save(p);

            Resource resource = new ClassPathResource("File/CTC.jrxml");

            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(Collections.singleton(p));
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(resource.getURL().getPath()));

            HashMap<String, Object> map = new HashMap<>();
            JasperPrint report = JasperFillManager.fillReport(compileReport, map, beanCollectionDataSource);

            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=Contrat.pdf");


            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
        }


    }

    @GetMapping("/Contrat/Decision/{id}")
    @PreAuthorize("hasAnyAuthority('admin','RH','Pointeur')")
    public ResponseEntity<byte[]> generateDecision(@PathVariable("id") Long id) throws IOException, JRException {

        Ajout p = ajoutRepository.getPersonForContract(id);

        Affaire affaire = affaireRepository.findByCode(p.getCodechantier());
        if (affaire.getNewDesign() == null){
            System.out.println("new Design is null");
            return null;
        }else {
            String directory = p.getNom().replaceAll("\\s", "") + p.getPrenom().replaceAll("\\s", "");
            Path root = Paths.get(globalVariableConfig.getGlobalVariable() + directory);
            if (Files.notExists(root)){
                Files.createDirectory(root);
            }
            p.setDesignation(affaire.getNewDesign());
            p.setState(IndividuState.A_Imprimer.name());
            ajoutRepository.save(p);

            Resource resource = new ClassPathResource("File/Attestation_Formation.jrxml");


            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(Collections.singleton(p));
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(resource.getURL().getPath()));

            HashMap<String, Object> map = new HashMap<>();
            JasperPrint report = JasperFillManager.fillReport(compileReport, map, beanCollectionDataSource);

            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=Attestation_Formation.pdf");



            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
        }
    }

    @GetMapping("/Contrat/DecisionEquipement/{id}")
    @PreAuthorize("hasAnyAuthority('admin','RH','Pointeur')")
    public ResponseEntity<byte[]> generateDecision2(@PathVariable("id") Long id) throws IOException, JRException {

        Ajout p = ajoutRepository.getPersonForContract(id);

        Affaire affaire = affaireRepository.findByCode(p.getCodechantier());
        if (affaire.getNewDesign() == null) {
            System.out.println("new Design is null");
            return null;
        } else {
            String directory = p.getNom().replaceAll("\\s", "") + p.getPrenom().replaceAll("\\s", "");
            Path root = Paths.get(globalVariableConfig.getGlobalVariable() + directory);
            if (Files.notExists(root)){
                Files.createDirectory(root);
            }

            p.setDesignation(affaire.getNewDesign());
            p.setState(IndividuState.A_Imprimer.name());
            ajoutRepository.save(p);

            Resource resource = new ClassPathResource("File/Equipement.jrxml");

            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(Collections.singleton(p));
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(resource.getURL().getPath()));

            HashMap<String, Object> map = new HashMap<>();
            JasperPrint report = JasperFillManager.fillReport(compileReport, map, beanCollectionDataSource);

            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=DecisionEquipement.pdf");



            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
        }
    }




}
