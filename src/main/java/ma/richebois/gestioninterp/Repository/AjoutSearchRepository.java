package ma.richebois.gestioninterp.Repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import ma.richebois.gestioninterp.DTO.AjoutSearchDTO;
import ma.richebois.gestioninterp.Model.Affaire;
import ma.richebois.gestioninterp.Model.Ajout;
import ma.richebois.gestioninterp.Model.Login;
import ma.richebois.gestioninterp.Model.Role;
import ma.richebois.gestioninterp.Service.UserImpService;
@Repository
@RequiredArgsConstructor
public class AjoutSearchRepository {
	private final EntityManager em;
	@Autowired
	private UserImpService userImpService;
	@Autowired
	private AffaireRepository affaireRepository;
	
	public List<Ajout> searchAjout(AjoutSearchDTO ajoutDTO)
    {
    	CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Ajout> criteriaQuery = criteriaBuilder.createQuery(Ajout.class);
		List<Predicate> predicates = new ArrayList<>();
		
		Root<Ajout> root = criteriaQuery.from(Ajout.class);
		if(ajoutDTO.getMatricule() != "") 
		{
			Predicate matricule = criteriaBuilder.equal(root.get("matricule"), Integer.parseInt(ajoutDTO.getMatricule()));
			predicates.add(matricule);
		}
		if(ajoutDTO.getNom() != "" && ajoutDTO.getNom() != null) 
		{
			Predicate nom = criteriaBuilder.like(root.get("nom"), ajoutDTO.getNom());
			predicates.add(nom);
		}
		if(ajoutDTO.getPrenom() != "" && ajoutDTO.getPrenom() != null) 
		{
			Predicate prenom = criteriaBuilder.like(root.get("prenom"), ajoutDTO.getPrenom());
			predicates.add(prenom);
		}
		if(ajoutDTO.getCodecin() != "" && ajoutDTO.getCodecin() != null) 
		{
			Predicate codeCin = criteriaBuilder.like(root.get("codecin"), ajoutDTO.getCodecin());
			predicates.add(codeCin);
		}
		if(!getChantiersParRole().isEmpty()) 
		{	
			Predicate affairePredicate = criteriaBuilder.in(root.get("codechantier")).value(getChantiersParRole());
			predicates.add(affairePredicate);
		}
		
		Predicate queryPredicate = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		criteriaQuery.where(queryPredicate);
		TypedQuery<Ajout> query = em.createQuery(criteriaQuery);
	    
		return query.getResultList();
//	    private Date datenaissance;
//	    private Date dateentree;
//	    private String codecin;
//	    private String cnss;
//	    private String codechantier;
//	    private String typecontrat;
//	    private String fonction;
		
    }
	public List<String> getChantiersParRole()
	{
		Login login = userImpService.findbyusername();
        List<Role> roles = login.getRoles();
        List<String> codeChantier = new ArrayList<String>();
        List<Affaire> affaireList = login.getChantier();
        for (Role role : roles)
        {
            if (role.getType().equals("Pointeur"))
            {
                for (Affaire chantier:affaireList)
                {
                    codeChantier.add(chantier.getCode());
                }        
            }
        }
        return codeChantier;
	}

}
