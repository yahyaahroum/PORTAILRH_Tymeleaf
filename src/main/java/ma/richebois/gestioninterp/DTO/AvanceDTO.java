package ma.richebois.gestioninterp.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvanceDTO {

    private int matricule;
    private Double montant;
    private String chantier;
    private Date dateAvance;

}
