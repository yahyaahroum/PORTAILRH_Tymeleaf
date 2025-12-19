package ma.richebois.gestioninterp.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pointage extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date datePointage;

    private double nbrHeure;

    @ManyToOne
    @JoinColumn(name = "individu")
    private Individu emp;

    @ManyToOne
    @JoinColumn(name = "chantier",referencedColumnName = "code")
    private Affaire affaire;

    @Override
    public String toString() {
        return "Pointage{" +
                "id=" + id +
                ", datePointage=" + datePointage +
                ", nbrHeure=" + nbrHeure +
                ", emp=" + emp +
                ", affaire=" + affaire +
                '}';
    }
}
