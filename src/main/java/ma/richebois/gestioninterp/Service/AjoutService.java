package ma.richebois.gestioninterp.Service;

import ma.richebois.gestioninterp.Model.Ajout;

import ma.richebois.gestioninterp.Model.Individu;
import ma.richebois.gestioninterp.Model.Role;
import ma.richebois.gestioninterp.Model.STC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

public interface AjoutService {


    public List<Ajout> listImport(long id);
    public List<Ajout> getAll();
    public Ajout getByCin(String codecin);
    public Ajout savePerson(MultipartFile file1, MultipartFile file2, MultipartFile file3, MultipartFile file4, MultipartFile file5, MultipartFile file6, Ajout ajout,String pessai) throws IOException;
    public Ajout updatePerson(MultipartFile file1,MultipartFile file6, MultipartFile file2, MultipartFile file3, MultipartFile file4, MultipartFile file5,Ajout ajout) throws IOException;
    public Page<Ajout> findPaginatedAjout(Pageable pageable);
    public Ajout valider(long id,String salaire,String pessai);
    public Ajout rejeter(long id,String motifRejet);

    public Ajout findById(long id);
    public Boolean deletePerson(Long id) ;

    public Ajout personneContractUpload(Long id,MultipartFile file1, MultipartFile file2, MultipartFile file3, Optional<Ajout> ajout) throws IOException;

    public Integer getLastMAtricul(Ajout ajout);

    public List<Ajout> getPersonnesSaisie(String start,String end) throws ParseException;

    public Individu getIndividu(Long id);

    public List<Ajout> getPersonByState(String state);
    public Page<Ajout> findPaginatedAjoutByState(Pageable pageable,String state);
	public Page<Ajout> AjoutPage(Pageable pageable, List<Ajout> ajoutList);





}
