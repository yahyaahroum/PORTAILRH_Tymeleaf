package ma.richebois.gestioninterp.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ma.richebois.gestioninterp.Repository.VilleRepository;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AT extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date dateAT;

    private String placeAT;

    @ManyToOne
    @JoinColumn(name = "ville")
    private Ville villeAT;

    private String adresseAT;

    private String causeAT;

    private String typeBlessure;

    private boolean arretTravail;

    private int joursArT;

    private String numTele;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateDebutArT;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateFinArT;


    @ManyToOne
    @JoinColumn(name = "chantier")
    private Affaire chantierAT;

    @ManyToOne
    @JoinColumn(name = "individu")
    private Individu individu;

    @Override
    public String toString() {
        return "AT{" +
                "id=" + id +
                ", dateAT=" + dateAT +
                ", placeAT='" + placeAT + '\'' +
                ", villeAT=" + villeAT +
                ", adresseAT='" + adresseAT + '\'' +
                ", causeAT='" + causeAT + '\'' +
                ", typeBlessure='" + typeBlessure + '\'' +
                ", arretTravail=" + arretTravail +
                ", joursArT=" + joursArT +
                ", numTele='" + numTele + '\'' +
                ", dateDebutArT=" + dateDebutArT +
                ", dateFinArT=" + dateFinArT +
                ", chantierAT=" + chantierAT +
                ", individu=" + individu +
                '}';
    }
}
