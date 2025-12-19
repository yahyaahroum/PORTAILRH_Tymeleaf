package ma.richebois.gestioninterp.Service;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Model.*;
import ma.richebois.gestioninterp.Repository.AffaireRepository;
import ma.richebois.gestioninterp.Repository.ContratRepository;
import ma.richebois.gestioninterp.Repository.IndividuRepository;
import ma.richebois.gestioninterp.Repository.PointageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
@AllArgsConstructor
@Service
public class PointageImpService implements PointageService{
    private PointageRepository pointageRepository;
    private IndividuRepository individuRepository;
    private UserService userService;
    private AffaireRepository affaireRepository;
    private ContratRepository contratRepository;

    @Override
    public Page<Pointage> pointageList(Pageable pageable) {
        Login login = userService.findbyusername();
        List<Affaire> affaires = login.getChantier();
        for (Role role : login.getRoles()){
            if (role.getType().equals("Pointeur") || role.getType().equals("ChefProjet")){
                Page<Pointage> pointagePage = pointageRepository.findAllByAffaireInOrderByDatePointageDesc(pageable,affaires);
                return pointagePage;
            }else {
                Page<Pointage> pointagePage = pointageRepository.findByOrderByDatePointageDesc(pageable);
                return pointagePage;
            }

        }

        return null;
    }

    @Override
    public List<Pointage> pointages() {
        return pointageRepository.findAll();
    }

    @Override
    public int countPointageByChantier() {
        //Login login = userService.findbyusername();
        //List<Affaire> affaires = login.getChantier();
        // A Compléter !!!
        return 0;
    }

    @Override
    public void controlePointage(String dateStart,String dateEnd,String profile) throws ParseException {
    	List<Contrat> contrats = contratRepository.findAllByContratactifAndProfilecode(2, profile);
    	if(profile.equalsIgnoreCase("all")) 
    	{
    		contrats = contratRepository.findAllByContratactif(2);
		}
    	
        for (Contrat c : contrats) 
        {
        	
            Optional<Individu> individu = individuRepository.findByIndividu(c.getMatricule());
			if (!individu.isPresent())
            {
	            System.out.println("contrat : "+ c.getMatricule());
	            System.out.println("individu : "+ individu.get().getIndividu());
	            Date datestart = new SimpleDateFormat("yyyy-MM-dd").parse(dateStart);
	            Date dateend = new SimpleDateFormat("yyyy-MM-dd").parse(dateEnd);
	
	            Calendar start = Calendar.getInstance();
	            start.setTime(datestart);
	            Calendar end = Calendar.getInstance();
	            end.setTime(dateend);
	
	            Affaire affaire = null;
	
	            for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) 
	            {
	                System.out.println("Avant Pointage Date : "+date +" Individu :" +individu.get().getIndividu());
	                List<Pointage> p = pointageRepository.findAllByEmpAndDatePointageBetween(individu.get(),date,date);
	                if (!p.isEmpty()){
	                    affaire = p.get(0).getAffaire();
	                }
	                else 
	                {
	                	if(c.getDateembauche().compareTo(date)<=0) 
	                	{
		                    Pointage pointage = new Pointage();
		                    pointage.setDatePointage(date);
		                    if (affaire==null){
		                        pointage.setAffaire(null);
		                    }
		                    if (affaire!=null){
		                        pointage.setAffaire(affaire);
		                    }
		                    else if (!c.getCodechantier().equals("") || c.getCodechantier()!=null)
		                    {
		                        Affaire aff = affaireRepository.findByCode(c.getCodechantier());
		                        pointage.setAffaire(aff);
		                    }
		                    pointage.setEmp(individu.get());
		                    pointage.setNbrHeure(0);
		                    pointageRepository.save(pointage);
	                	}
	                }
	            }

            }

        }

    }

}
