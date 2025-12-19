package ma.richebois.gestioninterp.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Decharge extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateDecharge;

    private String statut;
    @ManyToOne
    @JoinColumn(name = "individu")
    private Individu individu;

    @ManyToOne
    @JoinColumn(name = "chantier")
    private Affaire chantier;

    @OneToMany(targetEntity = DechargeDet.class,mappedBy = "decharge")
    @JsonIgnore
    private List<DechargeDet> dechargeDetList;


    @Override
    public String toString() {
        return "Decharge{" +
                "id=" + id +
                ", dateDecharge=" + dateDecharge +
                ", individu=" + individu +
                ", chantier=" + chantier +
                ", dechargeDetList=" + dechargeDetList +
                '}';
    }
}
