package ma.richebois.gestioninterp.Model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter
@Table(name = "Affaire")
public class Affaire extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "code")
    private String code;

    @Column(name = "Designation")
    private String designation;


    @Column(name = "code_des")
    private String codedes;

    @Column(name = "status")
    private String status;

    @Column(name = "adresse")
    private String adresse;

    @Column(name = "charge_produit")
    private String chargeProduit;

    @Column(name = "nvDesign")
    private String newDesign;

    @Column(name = "tolerance" )
    private Integer tolerance;
    
    @Column(name= "etape")
    private String etape;


    @ManyToOne
    @JoinColumn(name = "ville")
    private Ville ville;




}
