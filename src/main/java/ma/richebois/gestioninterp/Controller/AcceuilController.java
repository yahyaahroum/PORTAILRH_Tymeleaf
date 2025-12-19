package ma.richebois.gestioninterp.Controller;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Config.GlobalVariableConfig;
import ma.richebois.gestioninterp.Enum.CongeState;
import ma.richebois.gestioninterp.Enum.DemandeState;
import ma.richebois.gestioninterp.Enum.IndividuState;
import ma.richebois.gestioninterp.Model.Login;
import ma.richebois.gestioninterp.Enum.STCState;
import ma.richebois.gestioninterp.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.ParseException;
@AllArgsConstructor
@Controller
public class AcceuilController {
    private UserImpService loginImpService;
    private AcceuilImpService acceuilImpService;




    @GetMapping("/Acceuil")
    public String getHome(Model model) throws ParseException {
        Login login = loginImpService.findbyusername();

        model.addAttribute("login",login);
        model.addAttribute("dossierAValider",acceuilImpService.getPersonByState(IndividuState.A_Valider.name()));
        model.addAttribute("dossierValide",acceuilImpService.getPersonByState(IndividuState.Validé.name()));
        model.addAttribute("dossierAImprimer",acceuilImpService.getPersonByState(IndividuState.A_Imprimer.name()));
        model.addAttribute("dossierImprime",acceuilImpService.getPersonByState(IndividuState.Imprimé.name()));
        model.addAttribute("StatAvalider",acceuilImpService.getStatistique(IndividuState.A_Valider.name()));
        model.addAttribute("StatValide",acceuilImpService.getStatistique(IndividuState.Validé.name()));
        model.addAttribute("StatAImprimer",acceuilImpService.getStatistique(IndividuState.A_Imprimer.name()));
        model.addAttribute("StatImprime",acceuilImpService.getStatistique(IndividuState.Imprimé.name()));

        model.addAttribute("stcAValider",acceuilImpService.getSTCByState(STCState.Saisie.name()));
        model.addAttribute("stcValide",acceuilImpService.getSTCByState(STCState.Validée.name()));
        model.addAttribute("stcApprouve",acceuilImpService.getSTCByState(STCState.Approuvée.name()));

        model.addAttribute("stcStaticAValider",acceuilImpService.getStaticSTC(STCState.Saisie.name()));
        model.addAttribute("stcStaticValide",acceuilImpService.getStaticSTC(STCState.Validée.name()));
        model.addAttribute("stcStaticApprouve",acceuilImpService.getStaticSTC(STCState.Approuvée.name()));
        model.addAttribute("personnePerDay",acceuilImpService.countPersonesPerDay());
        model.addAttribute("allPersonnePerChantier",acceuilImpService.countAllPersonesInChantier());

        model.addAttribute("congeSaisi",acceuilImpService.getCongeByState(CongeState.Saisie.name()));
        model.addAttribute("congedepose",acceuilImpService.getCongeByState(CongeState.Importée.name()));
        model.addAttribute("congeValid",acceuilImpService.getCongeByState(CongeState.Validée.name()));
        model.addAttribute("congeRefuse",acceuilImpService.getCongeByState(CongeState.Refusée.name()));

        model.addAttribute("statiCongeSaisi",acceuilImpService.getStaticConge(CongeState.Saisie.name()));
        model.addAttribute("staticongedepose",acceuilImpService.getStaticConge(CongeState.Importée.name()));
        model.addAttribute("staticongeValid",acceuilImpService.getStaticConge(CongeState.Validée.name()));
        model.addAttribute("staticongeRefuse",acceuilImpService.getStaticConge(CongeState.Refusée.name()));

        model.addAttribute("demandeSaisi",acceuilImpService.getDemandeByState(DemandeState.Demandée.name()));
        model.addAttribute("demandeEncours",acceuilImpService.getDemandeByState(DemandeState.En_cours_de_signature.name()));
        model.addAttribute("demandeValid",acceuilImpService.getDemandeByState(DemandeState.Validée.name()));
        model.addAttribute("demandeRefuse",acceuilImpService.getDemandeByState(DemandeState.Refusée.name()));

        model.addAttribute("staticDemandeSaisi",acceuilImpService.getStaticDemande(DemandeState.Demandée.name()));
        model.addAttribute("statidemandeEncours",acceuilImpService.getStaticDemande(DemandeState.En_cours_de_signature.name()));
        model.addAttribute("statidemandeValid",acceuilImpService.getStaticDemande(DemandeState.Validée.name()));
        model.addAttribute("statidemandeRefuse",acceuilImpService.getStaticDemande(DemandeState.Refusée.name()));

        return "Acceuil/Acceuil";

    }
}
