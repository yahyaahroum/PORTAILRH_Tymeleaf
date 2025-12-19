package ma.richebois.gestioninterp.Model;


import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Individu implements Serializable {

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

    @Column(name = "codecin")
    private String codecin;

    @Column(name = "cnss")
    private String cnss;

    @Column(name = "cimr")
    private String cimr;

    @Column(name = "sitfamiliale")
    private String sitfamiliale;

    @Column(name = "codepays")
    private String codepays;

    @Column(name = "adressepays")
    private String adressepays;
    @Column(name = "adresseville")
    private String adresseville;
    @Column(name = "adresserue")
    private String adresserue;
    @Column(name = "niveauetude")
    private String niveauetude;

    @Column(name = "tele")
    private String tele;

    @Column(name = "matricule")
    private int individu;

    @ManyToOne
    @JoinColumn(name = "service_code", referencedColumnName = "code")
    private Service service;




}
