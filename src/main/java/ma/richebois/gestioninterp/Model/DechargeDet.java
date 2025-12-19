package ma.richebois.gestioninterp.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DechargeDet extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int quantite;
    private String marque;
    private String model;

    @ManyToOne
    @JoinColumn(name = "materiel")
    private Materiel materiel;

    @ManyToOne
    @JoinColumn(name = "id_Decharge")
    private Decharge decharge;

}
