package ma.richebois.gestioninterp.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;


import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Ajout extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "matricule")
    private int matricule;

    @Column(name = "nom")
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "datenaissance")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date datenaissance;

    @Column(name = "dateentree")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateentree;

    @Column(name = "sexe",columnDefinition = "10 default 1")
    private String sexe;

    @Column(name = "codecin")
    private String codecin;
    @Column(name = "dateExpCin")

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateExpCin;

    @Column(name = "cnss")
    private String cnss;

    @Column(name = "cimr")
    private String cimr;

    @Column(name = "codechantier")
    private String codechantier;

    @Column(name = "typecontrat")
    private String typecontrat;

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

    @Column(name = "salaire")
    private Double salaire;

    @Column(name = "codecontrat")
    private int codecontrat;

    @Column(name = "fonction")
    private String fonction;

    @Column(name = "banque")
    private String banque;

    @Column(name = "rib")
    private String rib;

    @Column(name = "domiciliation")
    private String domiciliation;

    @Column(name = "pcharge")
    private int pcharge;

    @Column(name = "profile",columnDefinition = "10 default 2")
    private String profile;

    @Column(name = "niveauetude")
    private String niveauetude;

    @Column(name = "moderegl")
    private String moderegl;

    @Column(name = "Origine")
    private String origine;

    @Column(name = "numero_tele")
    private String numtele;

    @Column(name = "pessai")
    private String pessai;

    @Column(name = "exist")
    private boolean exist;

    @Column(name = "contrat_actif")
    private boolean cactif;

    @Column(name = "contrat_suspendu")
    private boolean csuspendu;

    @Column(name = "etat_individu")
    private String state;

    @Column(name = "designation")
    private String designation;

    @Column(name = "libelle")
    private String libelle;

    @Column(name = "motifRejet")
    private String motifRejet;

    @Column(name = "fcin")
    private Boolean fcin;

    @Column(name = "fanthrop")
    private Boolean fanthrop;

    @Column(name = "frib")
    private Boolean frib;

    @Column(name = "fcnss")
    private Boolean fcnss;

    @Column(name = "fcv")
    private Boolean fcv;

    @Column(name = "whatsapp")
    private Boolean whatsapp;

    @Column(name = "bulletin")
    private Boolean bulletin;


    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "id_import")
    private Import imp;


    public LocalDate getDateExpCin() {
        return dateExpCin;
    }

    public void setDateExpCin(LocalDate dateExpCin) {
        this.dateExpCin = dateExpCin;
    }



}
