package ma.richebois.gestioninterp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutorisationAbsenceDTO {
    private Integer matricule;
    private String nom;
    private String prenom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDate dateReprise;
    private Integer nbrJour;
    private String interime;
}
