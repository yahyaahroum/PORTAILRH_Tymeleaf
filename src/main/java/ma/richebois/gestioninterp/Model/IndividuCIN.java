package ma.richebois.gestioninterp.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "V_Individus_CIN")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividuCIN implements Serializable {

    @Id
    @Column(name = "INDIVIDU")
    private String matricule;

    @Column(name = "NOMFAMILLE")
    private String nom;

    @Column(name = "PRENOM")
    private String prenom;

    @Column(name = "UP_CIN")
    private String cin;
}
