package ma.richebois.gestioninterp.Controller;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Enum.DemandeState;
import ma.richebois.gestioninterp.Model.*;
import ma.richebois.gestioninterp.Repository.AjoutRepository;
import ma.richebois.gestioninterp.Repository.IndividuRepository;
import ma.richebois.gestioninterp.Repository.RoleRepository;
import ma.richebois.gestioninterp.Service.DemandeService;
import ma.richebois.gestioninterp.Service.UserImpService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
@AllArgsConstructor
@Controller
public class DemandeController {

    private DemandeService demandeService;
    private RoleRepository roleRepository;
    private UserImpService userImpService;
    private AjoutRepository ajoutRepository;
    private IndividuRepository individuRepository;


    @GetMapping("/Demande/Toutes")
    public String getAllDem(Model model,@RequestParam("page") Optional<Integer> page,
                            @RequestParam("size") Optional<Integer> size,@RequestParam("etat") String state){
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(30);
        Login login = userImpService.findbyusername();

        Page<Demande> listDemande =demandeService.findByEmpAndState(PageRequest.of(currentPage - 1, pageSize),state);
        Role employe = roleRepository.findByType("Employé");
        boolean numtel = false;
        Optional<Individu> individu = individuRepository.findByIndividu(login.getMatricule());
        List<Ajout> ajoutList = ajoutRepository.findAllByMatriculeOrderByIdDesc(login.getMatricule());

        if (individu.isPresent()) {
            // ===== GESTION DU TÉLÉPHONE =====
            if (individu.get().getTele()==null){
                if (!ajoutList.isEmpty()){
                    if (ajoutList.get(0).getNumtele().length()<10){
                        model.addAttribute("numTelBoolean", numtel);
                    }
                    if (ajoutList.get(0).getNumtele().length()>=10){
                        numtel = true;
                        model.addAttribute("numTelBoolean", numtel);
                    }
                }
            }
            if (individu.get().getTele()!=null){
                numtel = true;
                model.addAttribute("numTelBoolean", numtel);
            }

            // ===== GESTION DU SERVICE ET LISTE INTÉRIME =====
            System.out.println("============ DEBUG COMPLET ============");
            System.out.println("🔍 Individu ID: " + individu.get().getId());
            System.out.println("🔍 Individu Nom: " + individu.get().getNom());
            System.out.println("🔍 Individu Prénom: " + individu.get().getPrenom());
            System.out.println("🔍 Individu Matricule: " + individu.get().getIndividu());

            // Forcer le chargement du service
            Service service = individu.get().getService();
            if (service != null) {
                System.out.println("✅ Service NON NULL");
                System.out.println("🔍 Service Code: " + service.getCode());
                System.out.println("🔍 Service Nom: " + service.getNom());

                // Tester la requête directement
                List<Individu> listeIndividus = individuRepository.findAllByServiceOrderByNom(service);
                System.out.println("🔍 Nombre d'individus trouvés: " + listeIndividus.size());

                if (listeIndividus.isEmpty()) {
                    System.out.println("⚠️ LISTE VIDE - Test avec requête native");
                    List<Individu> tousLesIndividus = individuRepository.findAll();
                    System.out.println("🔍 Total individus dans la BDD: " + tousLesIndividus.size());
                } else {
                    System.out.println("📋 Liste des individus du service " + service.getCode() + ":");
                    for (Individu ind : listeIndividus) {
                        System.out.println("   → " + ind.getNom() + " " + ind.getPrenom() +
                                " (Service: " + (ind.getService() != null ? ind.getService().getCode() : "NULL") + ")");
                    }
                }

                model.addAttribute("indByServ", listeIndividus);
            } else {
                System.out.println("❌ Service est NULL !");
                System.out.println("🔍 Tentative de récupération manuelle...");

                // Test : récupérer l'individu avec une requête personnalisée
                Individu ind2 = individuRepository.findAllByIndividuOrderByIndividuDesc(individu.get().getIndividu());
                if (ind2 != null && ind2.getService() != null) {
                    System.out.println("✅ Service trouvé avec requête alternative: " + ind2.getService().getCode());
                    List<Individu> listeIndividus = individuRepository.findAllByServiceOrderByNom(ind2.getService());
                    System.out.println("🔍 Nombre d'individus: " + listeIndividus.size());
                    model.addAttribute("indByServ", listeIndividus);
                } else {
                    System.out.println("❌ Toujours NULL même avec requête alternative");
                }
            }
            System.out.println("=======================================");

        } else {
            // L'individu n'existe pas dans la table, définir des valeurs par défaut
            System.out.println("❌ ERREUR: Individu non trouvé pour le matricule " + login.getMatricule());
            model.addAttribute("numTelBoolean", false);
        }

        model.addAttribute("numTelBoolean", numtel);
        model.addAttribute("listDemande",listDemande);
        model.addAttribute("state",state);
        model.addAttribute("role",employe);
        model.addAttribute("login",login);

        int totalPages = listDemande.getTotalPages();
        currentPage = listDemande.getNumber() + 1;
        int maxPagesToShow = 6;

        int startPage = Math.max(1, currentPage - 3);
        int endPage = Math.min(totalPages, startPage + maxPagesToShow - 1);

        if ((endPage - startPage + 1) < maxPagesToShow) {
            startPage = Math.max(1, endPage - maxPagesToShow + 1);
        }

        List<Integer> pageNumbers = IntStream.rangeClosed(startPage, endPage).boxed().collect(Collectors.toList());
        model.addAttribute("pageNumbers", pageNumbers);

        model.addAttribute("typeDemandes",demandeService.getAllType());
        return "Demande/ListDemande";
    }

    @PostMapping("/Demande/Ajouter")
    public String saveDemande(Demande demande,String numTele){
        Demande demande1 = demandeService.saveDemande(demande,numTele);
        demandeService.sendEmailDemande(demande1);
        return "redirect:/Demande/Toutes?etat=All";
    }

    @GetMapping("/Demande/Supprimer/{id}")
    public String deleteDemande(@PathVariable("id") Long id){
        demandeService.deleteDemande(id);
        return "redirect:/Demande/Toutes?etat=All";
    }

    @PostMapping("/Demande/Valider/{id}")
    public String validDemande(@PathVariable("id") Long id){
        demandeService.valider(id);
        return "redirect:/Demande/Toutes?etat=All";
    }

    @PostMapping("/Demande/Refuser/{id}")
    public String RefusDemande(@PathVariable("id") Long id, String motifRefus){
        demandeService.refuser(id,motifRefus);
        return "redirect:/Demande/Toutes?etat=All";
    }

    @GetMapping("/Demande/Generer/{id}")
    public ResponseEntity<byte[]> generateDemande(@PathVariable("id") Long id) throws JRException, IOException {
        return demandeService.generateDemande(id);
    }

}