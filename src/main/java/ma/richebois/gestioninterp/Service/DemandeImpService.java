package ma.richebois.gestioninterp.Service;

import java.time.ZoneOffset;
import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.DTO.CongeDTO;
import ma.richebois.gestioninterp.DTO.DemandeDTO;
import ma.richebois.gestioninterp.Domaine.MyConstants;
import ma.richebois.gestioninterp.Enum.DemandeState;
import ma.richebois.gestioninterp.Model.*;
import ma.richebois.gestioninterp.Repository.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import ma.richebois.gestioninterp.DTO.AutorisationAbsenceDTO;
import java.time.temporal.ChronoUnit;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.*;
import java.util.*;

@AllArgsConstructor
@Service
public class DemandeImpService implements DemandeService {

    private DemandeRepository demandeRepository;
    private TypeDemandeRepository typeDemandeRepository;
    private UserImpService userImpService;
    private IndividuRepository individuRepository;
    private ContratRepository contratRepository;
    private RoleRepository roleRepository;
    private LoginRepository loginRepository;
    public JavaMailSender emailSender;

    @Override
    public Demande saveDemande(Demande demande, String numTele) {
        Login login = userImpService.findbyusername();
        Optional<Individu> individu = individuRepository.findByIndividu(login.getMatricule());

        // ✅ CORRECTION : Vérifier si l'individu existe avant d'accéder à ses propriétés
        if (individu.isPresent()) {
            individu.get().setTele(numTele);
            individuRepository.save(individu.get());
        }

        demande.setEtat(DemandeState.Demandée.name());
        demande.setEmp(login);
        if (demande.getTypeDemande().getType().equals("Demande congé")) {
            if (demande.getDateFin().before(demande.getDateDebut())) {
                return null;
            }
        }
        Demande demande1 = demandeRepository.save(demande);
        return demande1;
    }

    @Override
    public Page<Demande> getAllByEmpPeagable(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Demande> listCons;

        if (getAllByEmp().size() < startItem) {
            listCons = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, getAllByEmp().size());
            listCons = getAllByEmp().subList(startItem, toIndex);
        }
        Page<Demande> consultPage = new PageImpl<Demande>(listCons, PageRequest.of(currentPage, pageSize),
                getAllByEmp().size());

        return consultPage;
    }

    @Override
    public List<Demande> getAllByEmp() {
        Login login = userImpService.findbyusername();
        List<Role> roles = login.getRoles();
        for (Role role : roles) {
            if (role.getType().equals("employé")) {
                List<Demande> getdemandeByRole = demandeRepository.findAllByEmpOrderByIdDesc(login);
                return getdemandeByRole;
            } else
                return demandeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        }
        return null;
    }

    @Override
    public List<TypeDemande> getAllType() {

        return typeDemandeRepository.findAll();
    }

    @Override
    public boolean deleteDemande(Long id) {
        demandeRepository.deleteById(id);
        return true;
    }

    @Override
    public Demande valider(Long id) {
        Optional<Demande> demande = demandeRepository.findById(id);
        demande.get().setEtat(DemandeState.Validée.name());
        return demandeRepository.save(demande.get());
    }

    @Override
    public Demande refuser(Long id, String motifRefus) {
        Optional<Demande> demande = demandeRepository.findById(id);
        demande.get().setEtat(DemandeState.Refusée.name());
        demande.get().setMotifRefus(motifRefus);
        return demandeRepository.save(demande.get());
    }

    @Override
    public List<Demande> getAllByEmpAndState(String state) {
        Login login = userImpService.findbyusername();
        List<Role> roles = login.getRoles();
        for (Role role : roles) {
            if (role.getType().equals("Employé")) {
                if (state.equals("All")) {
                    List<Demande> getdemandeByRole = demandeRepository.findAllByEmpOrderByIdDesc(login);
                    return getdemandeByRole;
                }
                if (state.equals("Demandée")) {
                    List<Demande> getdemandeByRoleAndState = demandeRepository.findAllByEmpAndEtatOrderByIdDesc(login,
                            DemandeState.Demandée.name());
                    return getdemandeByRoleAndState;
                }
                if (state.equals("Validée")) {
                    List<Demande> getdemandeByRoleAndState = demandeRepository.findAllByEmpAndEtatOrderByIdDesc(login,
                            DemandeState.Validée.name());
                    return getdemandeByRoleAndState;
                }
                if (state.equals("Refusée")) {
                    List<Demande> getdemandeByRoleAndState = demandeRepository.findAllByEmpAndEtatOrderByIdDesc(login,
                            DemandeState.Refusée.name());
                    return getdemandeByRoleAndState;
                }
                if (state.equals("En_cours_de_signature")) {
                    List<Demande> getdemandeByRoleAndState = demandeRepository.findAllByEmpAndEtatOrderByIdDesc(login,
                            DemandeState.En_cours_de_signature.name());
                    return getdemandeByRoleAndState;
                }

            }
            if (!role.getType().equals("Employé")) {
                if (role.getType().equals("RH") || role.getType().equals("admin")) {
                    if (state.equals("All")) {
                        return demandeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
                    }
                    if (state.equals("Demandée")) {
                        return demandeRepository.findAllByEtatOrderByIdDesc(DemandeState.Demandée.name());
                    }
                    if (state.equals("Validée")) {
                        return demandeRepository.findAllByEtatOrderByIdDesc(DemandeState.Validée.name());
                    }
                    if (state.equals("Refusée")) {
                        return demandeRepository.findAllByEtatOrderByIdDesc(DemandeState.Refusée.name());
                    }
                    if (state.equals("En_cours_de_signature")) {
                        List<Demande> getdemandeByRoleAndState = demandeRepository
                                .findAllByEtatOrderByIdDesc(DemandeState.En_cours_de_signature.name());
                        return getdemandeByRoleAndState;
                    }
                }

            }

        }
        return null;
    }

    @Override
    public Page<Demande> findByEmpAndState(Pageable pageable, String state) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Demande> listCons;

        if (getAllByEmpAndState(state).size() < startItem) {
            listCons = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, getAllByEmpAndState(state).size());
            listCons = getAllByEmpAndState(state).subList(startItem, toIndex);
        }
        Page<Demande> consultPage = new PageImpl<Demande>(listCons, PageRequest.of(currentPage, pageSize),
                getAllByEmpAndState(state).size());

        return consultPage;
    }

    @Override
    public ResponseEntity<byte[]> generateDemande(Long id) throws JRException, IOException {

        Optional<Demande> demande = demandeRepository.findById(id);
        Individu individu = individuRepository
                .findAllByIndividuOrderByIndividuDesc(demande.get().getEmp().getMatricule());
        List<Contrat> contrat = contratRepository.findAllByMatriculeOrderByNumcontratDesc(individu.getIndividu());

        if (demande.get().getTypeDemande().getType().equals("Demande congé") && individu != null
                && !contrat.isEmpty()) {
            CongeDTO congeDTO = new CongeDTO();

            // ✅ CORRECTION : Utiliser LocalDate au lieu de Calendar
            LocalDate dateFinMission = convertToLocalDate(demande.get().getDateFin());
            LocalDate dateReprise = dateFinMission.plusDays(1);

            // Si la date de reprise tombe un dimanche, ajouter un jour
            if (dateReprise.getDayOfWeek() == DayOfWeek.SUNDAY) {
                dateReprise = dateReprise.plusDays(1);
            }

            congeDTO.setMatricule(individu.getIndividu());
            congeDTO.setNom(individu.getNom());
            congeDTO.setPrenom(individu.getPrenom());
            congeDTO.setAdresserue(individu.getAdresserue());

            // ✅ Conversion Date → LocalDate pour éviter le problème timezone
            congeDTO.setDateentree(convertToLocalDate(contrat.get(0).getDateembauche()));
            congeDTO.setDateDebut(convertToLocalDate(demande.get().getDateDebut()));
            congeDTO.setDateFin(convertToLocalDate(demande.get().getDateFin()));
            congeDTO.setDateReprise(dateReprise);

            congeDTO.setMotif(demande.get().getMotif());
            congeDTO.setException(demande.get().getException());

            if (individu.getTele() != null) {
                congeDTO.setNumTele(individu.getTele());
            }

            congeDTO.setNbrJour(CalculNombreDeJoursDeConge.CalculNombreDeJours(
                    congeDTO,
                    demande.get().getDateDebut(),
                    demande.get().getDateFin()).getNbrJour());

            if (demande.get().getInterime() != null) {
                String nom = demande.get().getInterime().getNom() != null ? demande.get().getInterime().getNom().trim()
                        : "";
                String prenom = demande.get().getInterime().getPrenom() != null
                        ? demande.get().getInterime().getPrenom().trim()
                        : "";

                if (!nom.isEmpty() && !prenom.isEmpty()) {
                    congeDTO.setInterime(nom + " " + prenom);
                } else if (!nom.isEmpty()) {
                    congeDTO.setInterime(nom);
                } else if (!prenom.isEmpty()) {
                    congeDTO.setInterime(prenom);
                } else {
                    congeDTO.setInterime("Non renseigné");
                }
            } else {
                congeDTO.setInterime("Non renseigné");
            }

            Resource resource = new ClassPathResource("File/DemandeCongeEmp.jrxml");

            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(
                    Collections.singleton(congeDTO));
            JasperReport compileReport = JasperCompileManager
                    .compileReport(new FileInputStream(resource.getURL().getPath()));

            HashMap<String, Object> map = new HashMap<>();
            JasperPrint report = JasperFillManager.fillReport(compileReport, map, beanCollectionDataSource);

            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "inline;filename=Demande_Congé_" + congeDTO.getNom().trim().toUpperCase() + "_"
                            + congeDTO.getPrenom().trim().toUpperCase() + ".pdf");

            if (!demande.get().getEtat().equals("Validée") && !demande.get().getEtat().equals("Refusée")) {
                demande.get().setEtat(DemandeState.En_cours_de_signature.name());
                demandeRepository.save(demande.get());
            }

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
        }

        if (demande.get().getTypeDemande().getType().equals("Demande Attestation de travail") && individu != null
                && !contrat.isEmpty()) {
            List<Object[]> demandeObject = demandeRepository.getDemande(demande.get().getId());
            DemandeDTO demandeDTO = new DemandeDTO();

            demandeDTO.setNom((String) demandeObject.get(0)[1]);
            demandeDTO.setPrenom((String) demandeObject.get(0)[3]);
            demandeDTO.setLibelle((String) demandeObject.get(0)[2]);
            demandeDTO.setDateentree((Date) demandeObject.get(0)[4]);
            demandeDTO.setCodecin((String) demandeObject.get(0)[6]);
            demandeDTO.setCnss((String) demandeObject.get(0)[7]);

            Resource resource = new ClassPathResource("File/ATTESTATIONTRAVAIL.jrxml");

            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(
                    Collections.singleton(demandeDTO));
            JasperReport compileReport = JasperCompileManager
                    .compileReport(new FileInputStream(resource.getURL().getPath()));

            HashMap<String, Object> map = new HashMap<>();
            JasperPrint report = JasperFillManager.fillReport(compileReport, map, beanCollectionDataSource);

            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "inline;filename=Demande_Attestation_Travail_" + demandeDTO.getNom().trim().toUpperCase() + "_"
                            + demandeDTO.getPrenom().trim().toUpperCase() + ".pdf");

            if (!demande.get().getEtat().equals("Validée") && !demande.get().getEtat().equals("Refusée")) {
                demande.get().setEtat(DemandeState.En_cours_de_signature.name());
                demandeRepository.save(demande.get());
            }
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
        }

        // ========== GÉNÉRATION PDF AUTORISATION D'ABSENCE ==========
        if (demande.get().getTypeDemande().getType().equals("Autorisation d'absence") && individu != null) {
            AutorisationAbsenceDTO absenceDTO = new AutorisationAbsenceDTO();

            // Calculer la date de reprise (lendemain + éviter dimanche)
            LocalDate dateFinMission = convertToLocalDate(demande.get().getDateFin());
            LocalDate dateReprise = dateFinMission.plusDays(1);
            if (dateReprise.getDayOfWeek() == DayOfWeek.SUNDAY) {
                dateReprise = dateReprise.plusDays(1);
            }

            // Calculer le nombre de jours
            long nombreJours = java.time.temporal.ChronoUnit.DAYS.between(
                    convertToLocalDate(demande.get().getDateDebut()),
                    convertToLocalDate(demande.get().getDateFin())) + 1;

            // Remplir le DTO
            absenceDTO.setMatricule(individu.getIndividu());
            absenceDTO.setNom(individu.getNom());
            absenceDTO.setPrenom(individu.getPrenom());
            absenceDTO.setDateDebut(convertToLocalDate(demande.get().getDateDebut()));
            absenceDTO.setDateFin(convertToLocalDate(demande.get().getDateFin()));
            absenceDTO.setDateReprise(dateReprise);
            absenceDTO.setNbrJour((int) nombreJours);

            if (demande.get().getInterime() != null) {
                String nom = demande.get().getInterime().getNom() != null ? demande.get().getInterime().getNom().trim()
                        : "";
                String prenom = demande.get().getInterime().getPrenom() != null
                        ? demande.get().getInterime().getPrenom().trim()
                        : "";

                if (!nom.isEmpty() && !prenom.isEmpty()) {
                    absenceDTO.setInterime(nom + " " + prenom);
                } else if (!nom.isEmpty()) {
                    absenceDTO.setInterime(nom);
                } else if (!prenom.isEmpty()) {
                    absenceDTO.setInterime(prenom);
                } else {
                    absenceDTO.setInterime("Non renseigné");
                }
            } else {
                absenceDTO.setInterime("Non renseigné");
            }

            // Charger le template Jasper
            Resource resource = new ClassPathResource("File/AutorisationAbsence.jrxml");

            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(
                    Collections.singleton(absenceDTO));
            JasperReport compileReport = JasperCompileManager
                    .compileReport(new FileInputStream(resource.getURL().getPath()));

            HashMap<String, Object> map = new HashMap<>();
            JasperPrint report = JasperFillManager.fillReport(compileReport, map, beanCollectionDataSource);

            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "inline;filename=Autorisation_Absence_" + absenceDTO.getNom().trim().toUpperCase() + "_"
                            + absenceDTO.getPrenom().trim().toUpperCase() + ".pdf");

            if (!demande.get().getEtat().equals("Validée") && !demande.get().getEtat().equals("Refusée")) {
                demande.get().setEtat(DemandeState.En_cours_de_signature.name());
                demandeRepository.save(demande.get());
            }

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
        if (date instanceof java.sql.Date) {
            // Pour java.sql.Date, utiliser la conversion via LocalDate directement
            return ((java.sql.Date) date).toLocalDate();
        } else {
            // Pour java.util.Date, utiliser Instant avec UTC
            return date.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
        }
    }

    @Override
    public void sendEmailDemande(Demande demande) {
        Role role = roleRepository.findByType("RH");
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        List<Login> rhusers = loginRepository.findAllByRolesIn(roles);
        for (Login user : rhusers) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(MyConstants.MY_EMAIL);
            message.setTo(user.getEmail());
            message.setSubject("Validation d'une demande sur PORTAILRH");
            message.setText("Bonjour \n \n " +
                    "       Merci de valider la demande : " + demande.getTypeDemande().getType().trim().toUpperCase()
                    + " préparé par : " + demande.getEmp().getNom().toUpperCase().trim() + " "
                    + demande.getEmp().getPrenom().toUpperCase().trim() + " . \n\n"
                    + "Sincéres salutations");
            this.emailSender.send(message);
        }
    }

    public LocalDate convertToDate(Date date) {
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDate convertDateToLocalDate(Date date) {
        Instant instant = date.toInstant();
        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate;
    }
}