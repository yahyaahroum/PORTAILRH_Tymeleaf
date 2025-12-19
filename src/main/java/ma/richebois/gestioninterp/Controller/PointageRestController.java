package ma.richebois.gestioninterp.Controller;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.DTO.PointageArrayDto;
import ma.richebois.gestioninterp.DTO.PointageDTO;
import ma.richebois.gestioninterp.Model.*;
import ma.richebois.gestioninterp.Repository.AffaireRepository;
import ma.richebois.gestioninterp.Repository.IndividuRepository;
import ma.richebois.gestioninterp.Repository.PointageRepository;
import ma.richebois.gestioninterp.Service.IndividuImpService;
import ma.richebois.gestioninterp.Service.PointageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
@AllArgsConstructor
@RestController
public class PointageRestController {

    private IndividuImpService individuImpService;
    private IndividuRepository individuRepository;
    private PointageRepository pointageRepository;
    private AffaireRepository affaireRepository;
    private PointageService pointageService;


    @GetMapping (value = "/Individu/All")
    public List<Individu> getPointagePage(){
        return individuImpService.individuContratActif();
    }

    @GetMapping(value = "/Pointage/All")
    public Page<Pointage> pointagePage(Pageable pageable){
        return pointageService.pointageList(pageable);
    }

    @PostMapping(value = "/Pointage/Ajouter")
    public List<Pointage> pointageList(@RequestBody Pointage pointage, @RequestParam("emp") Set<Long> empId, @RequestParam("code") String code){
        List<Pointage> pointageList = new ArrayList<>();
        Affaire chantier = affaireRepository.findByCode(code);
        empId.forEach(empid -> {
            Optional<Individu> emp = individuRepository.findById(empid);
            List<Pointage> pointage1 = pointageRepository.findAllByAffaireAndDatePointageAndEmp(chantier,pointage.getDatePointage(),emp.get());
            if (pointage1.isEmpty()){
                Pointage p = new Pointage();
                p.setEmp(emp.get());
                p.setAffaire(chantier);
                p.setDatePointage(pointage.getDatePointage());
                p.setNbrHeure(pointage.getNbrHeure());
                pointageList.add(p);
            }
        });

        return pointageRepository.saveAll(pointageList);
    }

	/*
	 * @GetMapping(value = "/Pointage/delete/{id}") public boolean deletePointage
	 * (@PathVariable("id") Long id){ Optional<Pointage> pointage =
	 * pointageRepository.findById(id); pointageRepository.delete(pointage.get());
	 * return true; }
	 */

    @GetMapping(value = "/Pointage/get/{id}")
    public Pointage getPointage (@PathVariable("id") Long id){
        Optional<Pointage> pointage = pointageRepository.findById(id);
        return pointage.get();
    }

    @PostMapping(value = "/Pointage/edit/{id}")
    public Pointage editPointage (@PathVariable("id") Long id,@RequestBody Pointage p){
        Optional<Pointage> pointage = pointageRepository.findById(id);
        pointage.get().setNbrHeure(p.getNbrHeure());
        pointage.get().setDatePointage(p.getDatePointage());
        return pointageRepository.save(pointage.get());
    }

    @GetMapping(value = "/Pointage/Filter")
    public Page<Pointage> filterPointage(Pageable pageable,@RequestParam("Chantier") String code, @RequestParam("start") String start,@RequestParam("end") String end) throws ParseException {
        Date startDate=new SimpleDateFormat("yyyy-MM-dd").parse(start);
        Date endDate=new SimpleDateFormat("yyyy-MM-dd").parse(end);
        System.out.println(startDate+"----"+start);
        if (code.equals("all")){
            Page<Pointage> pointagePage = pointageRepository.findAllByDatePointageBetween(pageable,startDate,endDate);
            return pointagePage;
        } else if (!code.equals("all")){
            Affaire affaire = affaireRepository.findByCode(code);
            Page<Pointage> pointagePage = pointageRepository.findAllByAffaireAndDatePointageBetween(pageable,affaire,startDate,endDate);
            return pointagePage;
        }

        return null;
    }

    @GetMapping(value = "/Pointage/Controle")
    public void controlePointage (@RequestParam("start") String start,@RequestParam("end") String end,@RequestParam("profile") String profile) throws ParseException {
        pointageService.controlePointage(start,end,profile);
    }

    @PostMapping(value = "/Pointage/AjoutSimple")
    public List<Pointage> saveSimple(@RequestBody PointageArrayDto pointageArray) throws ParseException {
        for (PointageDTO p : pointageArray.getPointageDTOList()){

            Optional<Individu> individu = individuRepository.findById(p.getEmp());
            Affaire affaire = affaireRepository.findByCode(p.getChantier());
            Date datePointage=new SimpleDateFormat("yyyy-MM-dd").parse(p.getDatePointage());

            Pointage pointage = pointageRepository.findByEmpAndDatePointage(individu.get(),datePointage);

            if (pointage==null){
                Pointage pointage1 = new Pointage();
                pointage1.setDatePointage(datePointage);
                pointage1.setEmp(individu.get());
                pointage1.setNbrHeure(p.getNbrHeure());
                pointage1.setAffaire(affaire);
                System.out.println(pointage1.getAffaire().getCodedes()+" ----------- "+pointage1.getNbrHeure()+" ------------- "+pointage1.getDatePointage());
                pointageRepository.save(pointage1);
            }
            if (pointage!=null){

            }

        }
        return null;
    }


}
