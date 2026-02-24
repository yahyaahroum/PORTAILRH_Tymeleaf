package ma.richebois.gestioninterp.Controller;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Model.IndividuCIN;
import ma.richebois.gestioninterp.Repository.IndividuCINRepository;
import ma.richebois.gestioninterp.Service.CanvasExportUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AllArgsConstructor
@Controller
public class IndividuCINController {

    private final IndividuCINRepository individuCINRepository;
    private final CanvasExportUtils canvasExportUtils;

    @GetMapping("/Personne/ListeCIN")
    public String getListeCIN(Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam(value = "matricule", required = false) String matricule,
            @RequestParam(value = "nom", required = false) String nom,
            @RequestParam(value = "prenom", required = false) String prenom,
            @RequestParam(value = "cin", required = false) String cin) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);

        Page<IndividuCIN> individuPage = individuCINRepository.search(
                matricule, nom, prenom, cin,
                PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("individuPage", individuPage);
        model.addAttribute("matricule", matricule);
        model.addAttribute("nom", nom);
        model.addAttribute("prenom", prenom);
        model.addAttribute("cin", cin);

        int totalPages = individuPage.getTotalPages();
        if (totalPages > 0) {
            int visiblePages = 6;
            int startPage = Math.max(1, currentPage - 3);
            int endPage = Math.min(totalPages, startPage + visiblePages - 1);
            if ((endPage - startPage + 1) < visiblePages) {
                startPage = Math.max(1, endPage - visiblePages + 1);
            }
            List<Integer> pageNumbers = IntStream.rangeClosed(startPage, endPage).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "Personne/ListeCIN";
    }

    @GetMapping("/Personne/ExportCIN")
    public void exportToExcel(HttpServletResponse response,
            @RequestParam(value = "matricule", required = false) String matricule,
            @RequestParam(value = "nom", required = false) String nom,
            @RequestParam(value = "prenom", required = false) String prenom,
            @RequestParam(value = "cin", required = false) String cin) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Liste_Individus_CIN_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<IndividuCIN> list = individuCINRepository.findAllFiltered(matricule, nom, prenom, cin);
        canvasExportUtils.exportIndividusCIN(response, list);
    }
}
