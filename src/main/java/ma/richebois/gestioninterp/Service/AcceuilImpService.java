package ma.richebois.gestioninterp.Service;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Enum.CongeState;
import ma.richebois.gestioninterp.Enum.DemandeState;
import ma.richebois.gestioninterp.Enum.STCState;
import ma.richebois.gestioninterp.Model.*;
import ma.richebois.gestioninterp.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
@AllArgsConstructor
@Service
public class AcceuilImpService implements AcceuilService{

    private AjoutRepository ajoutRepository;
    private UserImpService loginImpService;
    private STCRepository stcRepository;
    private PointageRepository pointageRepository;
    private CongeRepository congeRepository;
    private DemandeRepository demandeRepository;

    @Override
    public Integer getPersonByState(String state) {

        Login login = loginImpService.findbyusername();

        List<Role> roles = login.getRoles();
        List<String> codechantier = new ArrayList<>();
        List<Affaire> chantierlist = login.getChantier();

        for (Role role:roles){
            if (role.getType().equals("Pointeur")||role.getType().equals("ChefProjet")){
                for (Affaire affaire:chantierlist){
                    codechantier.add(affaire.getCode());
                }
                Integer ajoutCount = ajoutRepository.countAllByOrigineAndStateAndCodechantierIn("Saisi", state,codechantier);
                return ajoutCount;
            }else
              return  ajoutRepository.countAllByOrigineAndState("Saisi",state);
        }

        return null;
    }

    @Override
    public Float getStatistique(String state) {

        Login login = loginImpService.findbyusername();
        Integer ajoutAllCountByState = ajoutRepository.countAllByOrigineAndState("Saisi",state);
        Integer ajoutAllCountAll = ajoutRepository.countAllByOrigine("Saisi");

        List<Role> roles = login.getRoles();
        List<String> codechantier = new ArrayList<>();
        List<Affaire> chantierlist = login.getChantier();

        for (Role role:roles){
            if (role.getType().equals("Pointeur")||role.getType().equals("ChefProjet")){
                for (Affaire affaire:chantierlist){
                    codechantier.add(affaire.getCode());
                    Integer ajoutCount = ajoutRepository.countAllByOrigineAndStateAndCodechantierIn("Saisi", state,codechantier);
                    Integer ajoutCountAll = ajoutRepository.countAllByOrigineAndCodechantierIn("Saisi",codechantier);
                    return (float) ajoutCount*100/ajoutCountAll;
                }
            }else

                return  (float) ajoutAllCountByState*100/ajoutAllCountAll;
        }

        return null;
    }

    @Override
    public Integer getSTCByState(String state) {

        Login login = loginImpService.findbyusername();
        List<Role> roles = login.getRoles();
        List<Affaire> chantierlist = login.getChantier();

        for (Role role:roles){
            if (role.getType().equals("Pointeur")||role.getType().equals("ChefProjet")){

                Integer ajoutCount = stcRepository.countAllByStateAndAffaireIn(state,chantierlist);
                return ajoutCount;

            }else
                return  stcRepository.countAllByState(state);
        }

        return null;
    }

    @Override
    public Float getStaticSTC(String state) throws ParseException {

        Integer CountAVALIDER = stcRepository.countAllByState(STCState.Saisie.name());
        Integer CountValide = stcRepository.countAllByState(STCState.Validée.name());
        Integer CountApprouve = stcRepository.countAllByState(STCState.Approuvée.name());



        Integer countAll = CountApprouve+CountAVALIDER+CountValide;

        Integer coutByState = stcRepository.countAllByState(state);

        Login login = loginImpService.findbyusername();
        List<Role> roles = login.getRoles();
        List<Affaire> chantierlist = login.getChantier();

        for (Role role:roles){
            if (role.getType().equals("Pointeur")||role.getType().equals("ChefProjet")){

                Integer stcCount = stcRepository.countAllByStateAndAffaireIn(state,chantierlist);
                Integer stcCountChantier = stcRepository.countAllByAffaireIn(login.getChantier());

                return (float)stcCount*100/stcCountChantier;

            }else
                return  (float) coutByState*100/countAll;
        }

        return null;
    }

    @Override
    public int countAllPersonesInChantier() {
        Login login = loginImpService.findbyusername();
        List<Role> roles = login.getRoles();
        List<Affaire> chantierlist = login.getChantier();

        for (Role role:roles){
            if (role.getType().equals("Pointeur")||role.getType().equals("ChefProjet")){
                return pointageRepository.countByPersonne(chantierlist);
            }else
                return pointageRepository.countByAllPersonne();
        }
        return 0;
    }

    @Override
    public int countPersonesPerDay(){
        Login login = loginImpService.findbyusername();
        List<Role> roles = login.getRoles();
        List<Affaire> chantierlist = login.getChantier();

        for (Role role:roles){
            if (role.getType().equals("Pointeur")||role.getType().equals("ChefProjet")){
                return pointageRepository.countByPersonneTODAY(chantierlist);
            }else

                return pointageRepository.countByAllPersonneTODAY();
        }
        return 0;
    }

    @Override
    public Map<String, Integer> charteEmbauche() {
        Map<String,Integer> dataEmbauche=new LinkedHashMap<String,Integer>();

        String date1 = "";
        String date2 = "";
        String date3 = "";
        String date4 = "";
        String date5 = "";
        String date6 = "";
        String date7 = "";
        String date8 = "";
        String date9 = "";
        String date10 = "";
        String date11= "";
        String date12 = "";

        SimpleDateFormat formatter1=new SimpleDateFormat("dd/MM/yyyy");

        return null;
    }

    @Override
    public Integer getCongeByState(String state) {
        Login login = loginImpService.findbyusername();
        List<Role> roles = login.getRoles();
        List<Affaire> chantierlist = login.getChantier();

        for (Role role:roles){
            if (role.getType().equals("Pointeur")||role.getType().equals("ChefProjet")){

                Integer ajoutCount = congeRepository.countAllByEtatAndAffaireIn(state,chantierlist);
                return ajoutCount;

            }else
                return  congeRepository.countAllByEtat(state);
        }

        return null;
    }

    @Override
    public Float getStaticConge(String state) throws ParseException {

        Integer CountSaisie = congeRepository.countAllByEtat(CongeState.Saisie.name());
        Integer CountValide = congeRepository.countAllByEtat(CongeState.Validée.name());
        Integer CountDepose = congeRepository.countAllByEtat(CongeState.Importée.name());
        Integer CountRefuse = congeRepository.countAllByEtat(CongeState.Refusée.name());



        Integer countAll = CountSaisie+CountValide+CountDepose+CountRefuse;

        Integer coutByState = congeRepository.countAllByEtat(state);

        Login login = loginImpService.findbyusername();
        List<Role> roles = login.getRoles();
        List<Affaire> chantierlist = login.getChantier();

        for (Role role:roles){
            if (role.getType().equals("Pointeur")||role.getType().equals("ChefProjet")){

                Integer congeCount = congeRepository.countAllByEtatAndAffaireIn(state,chantierlist);
                Integer congeCountChantier = congeRepository.countAllByAffaireIn(login.getChantier());

                return (float)congeCount*100/congeCountChantier;

            }else
                return  (float) coutByState*100/countAll;
        }

        return null;
    }

    @Override
    public Integer getDemandeByState(String state) {
        Login login = loginImpService.findbyusername();
        List<Role> roles = login.getRoles();
        for (Role role:roles){
            if (role.getType().equals("Employé")){

                Integer demandeCount = demandeRepository.countAllByEtatAndEmp(state,login);
                return demandeCount;

            }else
                return  demandeRepository.countAllByEtat(state);
        }

        return null;
    }

    @Override
    public Float getStaticDemande(String state) throws ParseException {
        Integer CountSaisie =  demandeRepository.countAllByEtat(DemandeState.Demandée.name());
        Integer CountEncours =  demandeRepository.countAllByEtat(DemandeState.En_cours_de_signature.name());
        Integer CountValider =  demandeRepository.countAllByEtat(DemandeState.Validée.name());
        Integer CountRefuser =  demandeRepository.countAllByEtat(DemandeState.Refusée.name());

        Integer countAll = CountSaisie+CountEncours+CountValider+CountRefuser;

        Integer coutByState = demandeRepository.countAllByEtat(state);

        Login login = loginImpService.findbyusername();
        List<Role> roles = login.getRoles();

        for (Role role:roles){
            if (role.getType().equals("Employé")){

                Integer demandeCount = demandeRepository.countAllByEtatAndEmp(state,login);
                Integer demandeCountByEmp = demandeRepository.countAllByEmp(login);

                return (float)demandeCount*100/demandeCountByEmp;

            }else
                return  (float) coutByState*100/countAll;
        }

        return null;
    }
}
