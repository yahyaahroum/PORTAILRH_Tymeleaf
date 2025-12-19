package ma.richebois.gestioninterp.Controller;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Model.Decharge;
import ma.richebois.gestioninterp.DTO.DechargeDto;
import ma.richebois.gestioninterp.Model.Materiel;
import ma.richebois.gestioninterp.Repository.MaterielRepository;
import ma.richebois.gestioninterp.Service.DechargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@AllArgsConstructor
@RestController
@RequestMapping("/Decharge")
public class DechargeRestController {
    private MaterielRepository materielRepository;
    private DechargeService dechargeService;


    @GetMapping("/Materiel/All")
    public List<Materiel> materielList(){
        return materielRepository.findAll(Sort.by(Sort.Direction.ASC,"type"));
    }

    @PostMapping(value = "/save")
    public Decharge saveDecharge(@RequestBody DechargeDto decharge) {

        System.out.println(decharge.toString());
        return dechargeService.saveDecharge(decharge);
    }
}
