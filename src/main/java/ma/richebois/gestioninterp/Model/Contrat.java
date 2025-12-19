package ma.richebois.gestioninterp.Model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Contrat implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "matricule")
    private int matricule;
    @Column(name = "numcontrat")
    private int numcontrat;

    @Column(name = "profile_code")
    private String profilecode;

    @Column(name = "dateentree")
    private Date dateembauche;

    @Column(name = "datesortie")
    private Date datesortie;

    @Column(name = "typecontrat")
    private String typecontrat;

    @Column(name = "chantier")
    private String codechantier;

    @Column(name = "modereglement")
    private String modereglement;

    @Column(name = "contratactif")
    private int contratactif;

    @Column(name = "suspendu")
    private int suspendu;
    
    @ManyToOne
    @JoinColumn(name = "fonction", referencedColumnName = "codefonction")
    private Fonction fonction;

}
