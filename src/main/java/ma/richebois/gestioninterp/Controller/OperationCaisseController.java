package ma.richebois.gestioninterp.Controller;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Enum.OperationState;
import ma.richebois.gestioninterp.Model.Affaire;
import ma.richebois.gestioninterp.DTO.AvanceDTO;
import ma.richebois.gestioninterp.Model.OperationCaisse;
import ma.richebois.gestioninterp.Repository.AffaireRepository;
import ma.richebois.gestioninterp.Repository.OperationCaisseRepository;
import ma.richebois.gestioninterp.Service.CanvasExportUtils;
import ma.richebois.gestioninterp.Service.IndividuImpService;
import ma.richebois.gestioninterp.Service.MotifImpService;
import ma.richebois.gestioninterp.Service.OperationCaisseImpService;
import ma.richebois.gestioninterp.Utils.PaginationUtils;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
@AllArgsConstructor
@Controller
public class OperationCaisseController {

    private OperationCaisseImpService operationCaisseImpService;
    private MotifImpService motifImpService;
    private AffaireRepository affaireRepository;
    private IndividuImpService individuImpService;
    private OperationCaisseRepository operationCaisseRepository;
    private CanvasExportUtils canvasExportUtils;

    @GetMapping("/OperationCaisse")
    public String operationCaisse(Model model,@RequestParam("page") Optional<Integer> page,
                                  @RequestParam("size") Optional<Integer> size){


        int currentPage = page.orElse(1);
        int pageSize = size.orElse(40);

        Page<OperationCaisse> operations = operationCaisseImpService.findPaginatedOperation(PageRequest.of(currentPage - 1, pageSize));
        List<OperationCaisse> operationCaisseList = operationCaisseRepository.findAll();

        model.addAttribute("operations",operations);

        int totalPages = operations.getTotalPages();
        if (totalPages >0){
            // ✅ pagination compacte
            List<Integer> pageNumbers = PaginationUtils.getCompactPageNumbers(currentPage, operations.getTotalPages(), 6);
            model.addAttribute("pageNumbers", pageNumbers);

            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("Motifs",motifImpService.getAllMotif());
        model.addAttribute("operationCaisse",operationCaisseList);
        model.addAttribute("Affaire",affaireRepository.findByEtapeNotInOrderByCodeAsc(Arrays.asList("PRA", "PRP", "ARC")));
        model.addAttribute("Individu",individuImpService.individuContratActif());


        return "OperationCaisse/OperationCaisseList";
    }

    @PostMapping("/OperationCaisse/Ajouter")
    @PreAuthorize("hasAnyAuthority('admin','RH','Caissier')")
    public ResponseEntity<byte[]> addOperation(OperationCaisse op,String code) throws JRException, IOException {

        if (code==null){

        }else if (code!=null) {

            Affaire chantier = affaireRepository.findByCode(code);

            op.setAffaire(chantier);
            op.setEtat(OperationState.Saisie.name());
            operationCaisseImpService.save(op);

            Resource resource = new ClassPathResource("File/Opercaisse.jrxml");

            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(Collections.singleton(op));
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(resource.getURL().getPath()));

            HashMap<String, Object> map = new HashMap<>();
            JasperPrint report = JasperFillManager.fillReport(compileReport, map, beanCollectionDataSource);

            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=Operation" + op.getId() + ".pdf");
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
        }
        return null;
    }

    @GetMapping("/Operation/Imprimer/{id}")
    @PreAuthorize("hasAnyAuthority('admin','RH','Caissier')")
    public ResponseEntity<byte[]> generateDecision(@PathVariable("id") Long id) throws IOException, JRException {

        Optional<OperationCaisse> p = operationCaisseRepository.findById(id);
        Resource resource = new ClassPathResource("File/Opercaisse.jrxml");

        JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(Collections.singleton(p.get()));
        JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(resource.getURL().getPath()));

        HashMap<String, Object> map = new HashMap<>();
        JasperPrint report = JasperFillManager.fillReport(compileReport, map, beanCollectionDataSource);

        byte[] data = JasperExportManager.exportReportToPdf(report);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=Operation"+p.get().getId()+".pdf");

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
    }

    @GetMapping("/Operation/ExportEV")
    @PreAuthorize("hasAnyAuthority('admin','RH')")
    public void exportToExcelSaisie( HttpServletResponse response,String start,String end) throws IOException, ParseException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Liste_Des_EV" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        List<OperationCaisse> operationCaisseList = operationCaisseImpService.getAllByDate(start, end);
        canvasExportUtils.exportEV(response,operationCaisseList);
    }

    @GetMapping("/PointageOperationCaisse")
    public String operationCaissePointage(Model model){
        return "OperationCaisse/PointageOperationCaisse";
    }

    @GetMapping("/Avance/ExportCanevas")
    @PreAuthorize("hasAnyAuthority('admin','RH')")
    public void exportAvanceTraitement( HttpServletResponse response,String start,String end) throws IOException, ParseException {

        List<AvanceDTO> avanceDTOS = operationCaisseImpService.traitementAvance(start,end);
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Liste_Des_Avances" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        canvasExportUtils.exportAvance(response,avanceDTOS);

    }

}
