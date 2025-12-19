package ma.richebois.gestioninterp.Model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="Inspecteur")
public class Inspecteur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "nom")
    private String nom;
    @Column(name = "prenom")
    private String prenom;
    @Column(name = "datenaissance")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date datenaissance;
    @Column(name = "sexe")
    private String sexe;
    @Column(name = "adresse")
    private String adresse;
    @Column(name="email")
    private String email;
    @Column(name = "tele")
    private String tele;



}
