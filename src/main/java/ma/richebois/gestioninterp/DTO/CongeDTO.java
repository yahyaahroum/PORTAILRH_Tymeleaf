package ma.richebois.gestioninterp.DTO;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CongeDTO {

    private LocalDate dateDebut;
    private LocalDate  dateReprise;
    private LocalDate  dateFin;
    private LocalDate  dateentree;
    private String motif;
    private String nom;
    private String prenom;
    private String adresserue;
    private Integer matricule;
    private String exception;
    private String numTele;
    private int nbrJour;
    private String interime;

}
