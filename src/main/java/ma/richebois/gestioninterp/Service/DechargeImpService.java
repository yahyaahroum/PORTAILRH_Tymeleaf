package ma.richebois.gestioninterp.Service;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Config.GlobalVariableConfig;
import ma.richebois.gestioninterp.DTO.DechargeDto;
import ma.richebois.gestioninterp.Enum.DechargeState;
import ma.richebois.gestioninterp.Model.*;
import ma.richebois.gestioninterp.Repository.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class DechargeImpService implements DechargeService{

    private DechargeRepository dechargeRepository;
    private MaterielRepository materielRepository;
    private AffaireRepository affaireRepository;
    private IndividuRepository individuRepository;
    private DechargeDetRepository dechargeDetRepository;
    private DechargePJRepository dechargePJRepository;
    private GlobalVariableConfig globalVariableConfig;

    // Méthode originale conservée pour compatibilité
    @Override
    public List<Decharge> getAllDecharge(String statut) {
        return getAllDecharge(statut, "");
    }

    // Nouvelle méthode avec recherche
    public List<Decharge> getAllDecharge(String statut, String search) {
        List<Decharge> decharges;

        if (statut.equals("All")){
            decharges = dechargeRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        } else {
            decharges = dechargeRepository.findAllByStatutOrderByIdDesc(statut);
        }

        // Filtrage par recherche
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase().trim();
            decharges = decharges.stream()
                    .filter(decharge -> {
                        // Recherche dans le nom/prénom de l'individu
                        boolean individuMatch = decharge.getIndividu() != null &&
                                (decharge.getIndividu().getNom() != null &&
                                        decharge.getIndividu().getNom().toLowerCase().contains(searchLower) ||
                                        decharge.getIndividu().getPrenom() != null &&
                                                decharge.getIndividu().getPrenom().toLowerCase().contains(searchLower));

                        // Recherche dans le code/désignation du chantier
                        boolean chantierMatch = decharge.getChantier() != null &&
                                (decharge.getChantier().getCode() != null &&
                                        decharge.getChantier().getCode().toLowerCase().contains(searchLower) ||
                                        decharge.getChantier().getDesignation() != null &&
                                                decharge.getChantier().getDesignation().toLowerCase().contains(searchLower));

                        // Recherche dans les types de matériel, marques et modèles
                        boolean materielMatch = decharge.getDechargeDetList() != null &&
                                decharge.getDechargeDetList().stream()
                                        .anyMatch(det ->
                                                (det.getMateriel() != null &&
                                                        det.getMateriel().getType() != null &&
                                                        det.getMateriel().getType().toLowerCase().contains(searchLower)) ||
                                                        (det.getMarque() != null &&
                                                                det.getMarque().toLowerCase().contains(searchLower)) ||
                                                        (det.getModel() != null &&
                                                                det.getModel().toLowerCase().contains(searchLower))
                                        );

                        return individuMatch || chantierMatch || materielMatch;
                    })
                    .collect(Collectors.toList());
        }

        return decharges;
    }

    @Override
    public Decharge saveDecharge(DechargeDto decharge){
        Optional<Individu> individu = individuRepository.findById(decharge.getIndividu());
        Optional<Affaire> affaire = affaireRepository.findById(decharge.getChantier());
        List<DechargeDet> dechargeDetList = new ArrayList<>();
        Decharge decharge1 = new Decharge();
        decharge1.setDateDecharge(decharge.getDateDecharge());
        decharge1.setIndividu(individu.get());
        decharge1.setChantier(affaire.get());
        decharge1.setStatut(DechargeState.BROUILLON.name());
        Decharge dechargeSave = dechargeRepository.save(decharge1);
        decharge.getDechargeDetList().forEach(dechargeDetDTO -> {
            Optional<Materiel> materiel = materielRepository.findById(dechargeDetDTO.getMateriel());
            DechargeDet dechargeDet = new DechargeDet();
            dechargeDet.setMateriel(materiel.get());
            dechargeDet.setQuantite(dechargeDetDTO.getQuantite());
            dechargeDet.setMarque(dechargeDetDTO.getMarque());
            dechargeDet.setModel(dechargeDetDTO.getModel());
            dechargeDet.setDecharge(dechargeSave);
            DechargeDet dechargeDet1 = dechargeDetRepository.save(dechargeDet);
            dechargeDetList.add(dechargeDet1);
        });
        dechargeSave.setDechargeDetList(dechargeDetList);

        dechargeRepository.save(dechargeSave);
        return dechargeSave;
    }

    @Override
    public List<Materiel> getAllMaterial() {
        return null;
    }

    @Override
    public ResponseEntity<byte[]> generateDecharge(long id) throws IOException, JRException {

        Optional<Decharge> decharge = dechargeRepository.findById(id);
        List<DechargeDet> dechargeDetList = dechargeDetRepository.findAllByDecharge(decharge.get());

        Resource resource = new ClassPathResource("File/Decharge.jrxml");

        JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(dechargeDetList);
        JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(resource.getURL().getPath()));

        HashMap<String, Object> map = new HashMap<>();
        JasperPrint report = JasperFillManager.fillReport(compileReport, map, beanCollectionDataSource);

        System.out.println("Décharge "+decharge.get().getIndividu().getNom().trim()+" "+decharge.get().getIndividu().getPrenom().trim()+".pdf");
        byte[] data = JasperExportManager.exportReportToPdf(report);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=Décharge_"+decharge.get().getIndividu().getNom().trim()+"_"+decharge.get().getIndividu().getPrenom().trim()+".pdf");
        if(decharge.get().getStatut().equals(DechargeState.BROUILLON.name()))
        {
            decharge.get().setStatut(DechargeState.IMPRIMEE.name());
            dechargeRepository.save(decharge.get());
        }

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
    }

    @Override
    public Decharge annuler(long id) {
        Optional<Decharge> decharge = dechargeRepository.findById(id);
        decharge.get().setStatut(DechargeState.ANNULEE.name());
        return dechargeRepository.save(decharge.get());
    }

    @Override
    public Decharge signer(long id,MultipartFile file) throws IOException {
        Optional<Decharge> decharge = dechargeRepository.findById(id);
        String directory = decharge.get().getIndividu().getNom().trim().replaceAll("\\s", "") + decharge.get().getIndividu().getPrenom().trim().replaceAll("\\s", "");
        Path root = Paths.get(globalVariableConfig.getGlobalVariable() + directory);
        Path rootDecharge = Paths.get(globalVariableConfig.getGlobalVariable() + directory+"/Decharge");
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");

        if (Files.notExists(root)) {
            Files.createDirectory(root);
        }
        if (Files.exists(root)){
            if (Files.notExists(rootDecharge)){
                Files.createDirectory(rootDecharge);
            }
        }

        DechargePJ dechargePJ = new DechargePJ();
        dechargePJ.setDecharge(decharge.get());
        dechargePJ.setName("Decharge_"+dateFormat.format(decharge.get().getDateDecharge())+"_"+directory+".pdf");
        Files.copy(file.getInputStream(), rootDecharge.resolve("Decharge_"+dateFormat.format(decharge.get().getDateDecharge())+"_"+directory+".pdf"), StandardCopyOption.REPLACE_EXISTING);
        dechargePJ.setDecharge(decharge.get());
        DechargePJ pj = dechargePJRepository.save(dechargePJ);
        decharge.get().setStatut(DechargeState.SIGNEE.name());
        return dechargeRepository.save(decharge.get());
    }

    @Override
    public Decharge liberer(long id) {
        Optional<Decharge> decharge = dechargeRepository.findById(id);
        decharge.get().setStatut(DechargeState.LIBEREE.name());
        return dechargeRepository.save(decharge.get());
    }

    // Méthode originale conservée pour compatibilité
    @Override
    public Page<Decharge> dechargeList(Pageable pageable, String statut) {
        return dechargeList(pageable, statut, "");
    }

    // Nouvelle méthode avec recherche
    public Page<Decharge> dechargeList(Pageable pageable, String statut, String search) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Decharge> listCons;

        List<Decharge> allDecharges = getAllDecharge(statut, search);

        if (allDecharges.size() < startItem) {
            listCons = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, allDecharges.size());
            listCons = allDecharges.subList(startItem, toIndex);
        }

        Page<Decharge> consultPage = new PageImpl<Decharge>(
                listCons,
                PageRequest.of(currentPage, pageSize),
                allDecharges.size()
        );

        return consultPage;
    }
}