package ma.richebois.gestioninterp.Model;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="orde_mission")
public class OrdreMission extends Auditable<String> implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name="dateOrdreMission")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOrdreMission;
    @Column(name="nombreJours")
    private Integer nombreJours;
    @Column(name="dateDebutMission")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateDebutMission;
    @Column(name="dateFinMission")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateFinMission;
    @Column(name="libelleMission")
    private String libelleMission;
    @Column(name="moyenTransport")
    private String moyenTransport;
    @Column(name="imprime")
    private boolean imprime;
    @ManyToOne
    @JoinColumn(name="id_login")
    private Login login;
    public OrdreMission(boolean imprime) {
        this.imprime = imprime;
    }
    @ManyToOne
    @JoinColumn(name="id_Affaire")
    private Affaire affaire;
    @ManyToOne
    @JoinColumn(name="id_individu")
    private Individu individu;


    public boolean getImpr(){
        return this.imprime;
    }
}
