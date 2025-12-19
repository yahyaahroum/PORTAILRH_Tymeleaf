package ma.richebois.gestioninterp.Service;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.DTO.CongeDTO;
import ma.richebois.gestioninterp.Domaine.MyConstants;
import ma.richebois.gestioninterp.Enum.CongeState;
import ma.richebois.gestioninterp.Model.*;
import ma.richebois.gestioninterp.Repository.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
@AllArgsConstructor
@Service
public class CongeImpService implements CongeService{

    private CongeRepository congeRepository;
    private UserImpService userImpService;
    private RoleRepository roleRepository;
    private LoginRepository loginRepository;
    public JavaMailSender emailSender;

    @Override
    public Conge save(Conge conge) {

        Integer countVerif = congeRepository.countByAffaireAndIndividuAndDateDebutAndDateFin(conge.getAffaire(), conge.getIndividu(), conge.getDateDebut(),conge.getDateFin());

        if (countVerif==0){
            Conge conge1 = congeRepository.save(conge);
            return conge1;
        }else if (conge.getDateFin().before(conge.getDateDebut())){
            return null;
        }else
            return null;
    }

    @Override
    public Conge saveDemande(long id, MultipartFile file) throws IOException {
        Optional<Conge> conge = congeRepository.findById(id);
        String directory = conge.get().getIndividu().getNom().trim().replaceAll("\\s", "") + conge.get().getIndividu().getPrenom().trim().replaceAll("\\s", "");
        Path root = Paths.get("/home/Portail-BETA-Conge/" + directory +"/");
        if (Files.notExists(root)) {
            Files.createDirectory(root);
        }
        Files.copy(file.getInputStream(), root.resolve("Demande_Conge" + "_" + conge.get().getIndividu().getCodecin().trim()+ "_" + directory + ".pdf"), StandardCopyOption.REPLACE_EXISTING);
        conge.get().setEtat(CongeState.Importée.name());
        return congeRepository.save(conge.get());
    }

    @Override
    public Conge valideConge(Long id) {
        Optional<Conge> conge = congeRepository.findById(id);
        conge.get().setEtat(CongeState.Validée.name());
        return congeRepository.save(conge.get());
    }

    @Override
    public Conge refusConge(Long id, String motifRefus) {
        Optional<Conge> conge = congeRepository.findById(id);
        conge.get().setEtat(CongeState.Refusée.name());
        conge.get().setMotifRefus(motifRefus);
        return congeRepository.save(conge.get());
    }

    @Override
    public boolean deleteConge(Long id) {
        congeRepository.deleteById(id);
        return true;
    }


    @Override
    public List<Conge> getListCongeByStateAndRole(String state) {

        Login login = userImpService.findbyusername();
        List<Role> roles= login.getRoles();
        List<String> states = new ArrayList<>();
        states.add(state);
        List<Affaire> chantierlist = login.getChantier();
        for (Role role : roles) {
            if (role.getType().equals("Pointeur") || role.getType().equals("ChefProjet") || role.getType().equals("RH")) {
                List<Conge> getCongebyRoleAndChantierwithState = congeRepository.findAllByAffaireInAndEtatInOrderByIdDesc(chantierlist,states);
                return getCongebyRoleAndChantierwithState;
            }else
                return congeRepository.findAllByEtatInOrderByIdDesc(states);
        }
        return null;
    }

    @Override
    public Page<Conge> findPaginatedConge(Pageable pageable, String state) {

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Conge> listCons;

        if (getListCongeByStateAndRole(state).size() < startItem) {
            listCons = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, getListCongeByStateAndRole(state).size());
            listCons = getListCongeByStateAndRole(state).subList(startItem, toIndex);
        }
        Page<Conge> consultPage = new PageImpl<Conge>(listCons, PageRequest.of(currentPage, pageSize), getListCongeByStateAndRole(state).size());

        return consultPage;
    }

/*    @Override
    public ResponseEntity<byte[]> generateDemandeConge(Long id) throws IOException, JRException {

        List<Object[]> getCongeForDemande = congeRepository.getIndividuForDemande(id);

        if (!getCongeForDemande.isEmpty()){
            Optional<Conge> conge = congeRepository.findById(id);



            CongeDTO congeDTO = new CongeDTO();

            Calendar cal = Calendar.getInstance();
            cal.setTime((Date) getCongeForDemande.get(0)[7]);
            cal.add(Calendar.DATE, 1);

            if (cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
                cal.add(Calendar.DATE, 1);
            }

            congeDTO.setMatricule((Integer) getCongeForDemande.get(0)[1]);
            congeDTO.setNom((String) getCongeForDemande.get(0)[2]);
            congeDTO.setPrenom((String) getCongeForDemande.get(0)[3]);
            congeDTO.setAdresserue((String) getCongeForDemande.get(0)[4]);
            congeDTO.setDateentree((Date) getCongeForDemande.get(0)[5]);
            congeDTO.setDateDebut((Date) getCongeForDemande.get(0)[6]);
            congeDTO.setDateFin((Date) getCongeForDemande.get(0)[7]);
            congeDTO.setMotif((String) getCongeForDemande.get(0)[8]);
            congeDTO.setException((String) getCongeForDemande.get(0)[10]);
            congeDTO.setInterime("NEANT");
            if (conge.get().getIndividu().getTele() != null){
                congeDTO.setNumTele(conge.get().getIndividu().getTele());
            }
            else
            	congeDTO.setNumTele("");
            congeDTO.setNbrJour(CalculNombreDeJoursDeConge.CalculNombreDeJours(congeDTO, congeDTO.getDateDebut(), congeDTO.getDateFin()).getNbrJour());
            congeDTO.setDateReprise(cal.getTime());

            Resource resource = new ClassPathResource("File/DemandeCongeEmp.jrxml");

            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(Collections.singleton(congeDTO));
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(resource.getURL().getPath()));

            HashMap<String, Object> map = new HashMap<>();
            JasperPrint report = JasperFillManager.fillReport(compileReport, map, beanCollectionDataSource);

            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=Demande_Congé_"+congeDTO.getNom().replaceAll("\\s", "").trim().toUpperCase()+"_"+congeDTO.getPrenom().replaceAll("\\s", "").trim().toUpperCase()+".pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
        }

        if (getCongeForDemande.isEmpty()){
            return null;
        }
        return null;

    }*/
@Override
public ResponseEntity<byte[]> generateDemandeConge(Long id) throws IOException, JRException {

    List<Object[]> getCongeForDemande = congeRepository.getIndividuForDemande(id);

    if (!getCongeForDemande.isEmpty()){
        Optional<Conge> conge = congeRepository.findById(id);

        CongeDTO congeDTO = new CongeDTO();

        // ✅ CORRECTION : Convertir Date en LocalDate pour éviter le problème timezone
        Date dateFin = (Date) getCongeForDemande.get(0)[7];
        LocalDate dateFinLocal = convertToLocalDate(dateFin);
        LocalDate dateReprise = dateFinLocal.plusDays(1);

        // Si la date de reprise tombe un dimanche, ajouter un jour
        if (dateReprise.getDayOfWeek() == DayOfWeek.SUNDAY) {
            dateReprise = dateReprise.plusDays(1);
        }

        congeDTO.setMatricule((Integer) getCongeForDemande.get(0)[1]);
        congeDTO.setNom((String) getCongeForDemande.get(0)[2]);
        congeDTO.setPrenom((String) getCongeForDemande.get(0)[3]);
        congeDTO.setAdresserue((String) getCongeForDemande.get(0)[4]);

        // ✅ CORRECTION : Convertir toutes les dates en LocalDate
        Date dateEntree = (Date) getCongeForDemande.get(0)[5];
        congeDTO.setDateentree(convertToLocalDate(dateEntree));

        Date dateDebut = (Date) getCongeForDemande.get(0)[6];
        congeDTO.setDateDebut(convertToLocalDate(dateDebut));

        congeDTO.setDateFin(convertToLocalDate(dateFin));
        congeDTO.setMotif((String) getCongeForDemande.get(0)[8]);
        congeDTO.setException((String) getCongeForDemande.get(0)[10]);
        congeDTO.setInterime("NEANT");

        if (conge.get().getIndividu().getTele() != null){
            congeDTO.setNumTele(conge.get().getIndividu().getTele());
        } else {
            congeDTO.setNumTele("");
        }

        congeDTO.setNbrJour(CalculNombreDeJoursDeConge.CalculNombreDeJours(
                congeDTO,
                dateDebut,  // Passer les Date originales à la méthode de calcul
                dateFin
        ).getNbrJour());

        // ✅ Date de reprise en LocalDate
        congeDTO.setDateReprise(dateReprise);

        Resource resource = new ClassPathResource("File/DemandeCongeEmp.jrxml");

        JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(Collections.singleton(congeDTO));
        JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(resource.getURL().getPath()));

        HashMap<String, Object> map = new HashMap<>();
        JasperPrint report = JasperFillManager.fillReport(compileReport, map, beanCollectionDataSource);

        byte[] data = JasperExportManager.exportReportToPdf(report);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=Demande_Congé_"+congeDTO.getNom().replaceAll("\\s", "").trim().toUpperCase()+"_"+congeDTO.getPrenom().replaceAll("\\s", "").trim().toUpperCase()+".pdf");

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
    }

    return null;
}

    // ✅ Méthode utilitaire pour convertir Date → LocalDate SANS timezone
    private LocalDate convertToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        // Conversion directe sans timezone (utilise java.sql.Date)
        return new java.sql.Date(date.getTime()).toLocalDate();
    }
    @Override
    public void showFiles(Long id, HttpServletResponse response) {
       Optional<Conge> conge = congeRepository.findById(id);
        String directory = conge.get().getIndividu().getNom().trim().replaceAll("\\s", "") + conge.get().getIndividu().getPrenom().trim().replaceAll("\\s", "");
       String folderPath = "/home/Portail-BETA-Conge/";
       String fileName = "Demande_Congé_"+conge.get().getIndividu().getCodecin().trim()+ "_" + directory+".pdf";

        if (fileName.indexOf(".pdf") > -1) response.setContentType("application/pdf");

        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setHeader("Content-Transfer-Encoding", "binary");

        try {
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            FileInputStream fis = new FileInputStream(folderPath +directory+ "/" + fileName);

            int len;
            byte[] buf = new byte[99999];
            while ((len = fis.read(buf)) > 0) {
                bos.write(buf, 0, len);
            }
            bos.close();
            response.flushBuffer();

        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    @Override
    public void sendEmailConge(Long id) {
        Optional<Conge> conge = congeRepository.findById(id);
        Role role = roleRepository.findByType("RH");
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        List<Login> rhusers = loginRepository.findAllByRolesIn(roles);
        for (Login user : rhusers) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(MyConstants.MY_EMAIL);
            message.setTo(user.getEmail());
            message.setSubject("Validation d'une demande congé sur PORTAILRH");
            message.setText("Bonjour \n \n " +
                    "       Merci de valider la demande : "+conge.get().getMotif().trim().toUpperCase()+" préparé par : " +conge.get().getCreatedBy().toUpperCase().trim()+" pour le demandeur : "+conge.get().getIndividu().getNom().trim().toUpperCase()+" "+conge.get().getIndividu().getPrenom().trim().toUpperCase()+" . \n\n"
                    +"Sincéres salutations");
            this.emailSender.send(message);
        }
    }

}
