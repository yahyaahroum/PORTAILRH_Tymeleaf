package ma.richebois.gestioninterp.DTO;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AjoutSearchDTO {

    private String matricule;
    private String nom;
    private String prenom;
    private Date datenaissance;
    private Date dateentree;
    private String codecin;
    private String cnss;
    private String codechantier;
    private String typecontrat;
    private String fonction;

}
