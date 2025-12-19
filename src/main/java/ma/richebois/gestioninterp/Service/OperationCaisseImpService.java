package ma.richebois.gestioninterp.Service;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.DTO.AvanceDTO;
import ma.richebois.gestioninterp.Model.*;
import ma.richebois.gestioninterp.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
@AllArgsConstructor
@Service
public class OperationCaisseImpService implements OperationCaisseService {
    private OperationCaisseRepository operationCaisseRepository;
    private ContratRepository contratRepository;
    private PointageRepository pointageRepository;
    private IndividuRepository individuRepository;


    @Override
    public List<OperationCaisse> getAll() {
        return operationCaisseRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
    }

    @Override
    public List<OperationCaisse> getAllByDate(String start,String end) throws ParseException {
        Date datestart = new SimpleDateFormat("yyyy-MM-dd").parse(start);
        Date dateend = new SimpleDateFormat("yyyy-MM-dd").parse(end);
        return operationCaisseRepository.findAllByDateOperBetween(datestart,dateend);
    }

    @Override
    public OperationCaisse save(OperationCaisse op) {
        return operationCaisseRepository.save(op);
    }

    @Override
    public Page<OperationCaisse> findPaginatedOperation(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<OperationCaisse> listCons;

        if (getAll().size() < startItem) {
            listCons = Collections.emptyList();
        } else {

            int toIndex = Math.min(startItem + pageSize, getAll().size());
            listCons = getAll().subList(startItem, toIndex);
        }
        Page<OperationCaisse> consultPage = new PageImpl<OperationCaisse>(listCons, PageRequest.of(currentPage, pageSize), getAll().size());


        return consultPage;
    }

    @Override
    public List<AvanceDTO> traitementAvance(String dateStart, String dateEnd) throws ParseException {

        List<AvanceDTO> avanceDTOS = new ArrayList<>();

        List<Contrat> contratList = contratRepository.findAllByContratactifAndProfilecode(2,"2");
        Set<Integer> listMatricule = new HashSet<>();

        contratList.forEach(contrat -> listMatricule.add(contrat.getMatricule()));

        Date datestart = new SimpleDateFormat("yyyy-MM-dd").parse(dateStart);
        Date dateend = new SimpleDateFormat("yyyy-MM-dd").parse(dateEnd);
        listMatricule.forEach(matricule -> {
            Optional<Individu> individu = individuRepository.findByIndividu(matricule);
            List<Pointage> pointageList = pointageRepository.findAllByEmpAndDatePointageBetween(individu.get(),datestart,dateend);

            if (!pointageList.isEmpty()){
                double nombreHeure = pointageList.stream().map(pointage -> pointage.getNbrHeure()).reduce((double) 0,Double::sum);

                if (nombreHeure!=0){
                    AvanceDTO avanceDTO = new AvanceDTO();
                    avanceDTO.setDateAvance(datestart);
                    avanceDTO.setMatricule(matricule);

                    if (nombreHeure*14.13>600.0){
                        avanceDTO.setMontant(600.0);
                    }
                    if (nombreHeure*14.13<600.0){
                        avanceDTO.setMontant((double)((int)(nombreHeure*14.13/100))*100);
                    }

                    if (!pointageList.isEmpty()){

                        if (pointageList.get(pointageList.size()-1).getAffaire()!=null){
                            avanceDTO.setChantier(pointageList.get(pointageList.size()-1).getAffaire().getCode());
                        }
                    }

                    if (pointageList.isEmpty()){
                        avanceDTO.setChantier("");
                    }
                    avanceDTOS.add(avanceDTO);
                }
            }
        });

        return avanceDTOS;
    }
}
