package ma.richebois.gestioninterp.DTO;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DemandeDTO {

    private String nom;
    private String prenom;
    private String codecin;
    private String cnss;
    private String libelle;
    private Date dateentree;


}
