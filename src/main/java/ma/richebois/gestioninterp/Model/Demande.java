package ma.richebois.gestioninterp.Model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Demande extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateDebut;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateFin;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateSouhaite;

    private String etat;

    private String motif;

    private String commentaire;

    private String motifRefus;

    private String exception;

    private String numTele;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM")
    private Date periodeDebut;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM")
    private Date periodeFin;


    private float mensualite;

    private float montant;

    private boolean cachete;

    @ManyToOne
    @JoinColumn(name = "employe")
    private Login emp;

    @ManyToOne
    @JoinColumn(name = "typeDemande")
    private TypeDemande typeDemande;

    @ManyToOne
    @JoinColumn(name = "interime")
    private Individu interime;





}
