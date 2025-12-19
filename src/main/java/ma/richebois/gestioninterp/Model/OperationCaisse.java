package ma.richebois.gestioninterp.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class OperationCaisse extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern="dd-MM-yyyy")
    private Date dateOper;

    private double montant;

    private String etat;

    @Column(name = "numJrs",nullable = true)
    private Integer numJrs;

    @ManyToOne(targetEntity = Affaire.class)
    @JoinColumn(name = "chantier", referencedColumnName = "code")
    private Affaire affaire;

    @ManyToOne
    @JoinColumn(name = "individu")
    private Individu individu;

    @ManyToOne
    @JoinColumn(name = "motif")
    private Motif motif;




}
