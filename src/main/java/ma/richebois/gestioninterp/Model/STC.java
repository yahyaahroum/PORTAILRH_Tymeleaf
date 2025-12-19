package ma.richebois.gestioninterp.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class STC extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "datedepart")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date datedepart;

    @Column(name = "listNoir")
    private boolean listNoir;

    @Column(name = "evalu")
    private String evalu;

    @Column(name = "etat")
    private String state;

    @Column(name = "telephone")
    private String telephone;


    @ManyToOne(targetEntity = Affaire.class)
    @JoinColumn(name = "chantier", referencedColumnName = "code")
    private Affaire affaire;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "individu")
    private Individu individu;




}
