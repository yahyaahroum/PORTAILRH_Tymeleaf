package ma.richebois.gestioninterp.Service;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Model.Contrat;
import ma.richebois.gestioninterp.Model.Individu;
import ma.richebois.gestioninterp.Repository.ContratRepository;
import ma.richebois.gestioninterp.Repository.IndividuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@Service
public class IndividuImpService implements IndividuService {
    private IndividuRepository individuRepository;


//    @Override
//    public List<Individu> individuContratActif() {
//
//        List<Individu> individuList = individuRepository.findAll(Sort.by(Sort.Direction.ASC,"nom"));
//        List<Individu> individus = new ArrayList<>();
//
//        for (Individu individu : individuList){
//            List<Contrat> contrats = contratRepository.findAllByMatriculeOrderByNumcontratDesc(individu.getIndividu());
//            if (contrats.size()==0){
//                continue;
//            }
//            else if (contrats.get(0).getContratactif()==2){
//                individus.add(individu);
//            }
//        }
//
//        return individus;
//    }

    @Override
    public List<Individu> individuContratActif() {

        List<Individu> individuList = individuRepository.getContratActif();

        return individuList;
    }
}
