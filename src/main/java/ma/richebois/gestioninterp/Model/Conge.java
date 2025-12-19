package ma.richebois.gestioninterp.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Conge extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateDebut;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateFin;

    private String etat;

    private String motif;

    private String motifRefus;

    private String exception;

    private String numTele;

    @ManyToOne(targetEntity = Affaire.class)
    @JoinColumn(name = "chantier", referencedColumnName = "code")
    private Affaire affaire;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "individu")
    private Individu individu;

}
