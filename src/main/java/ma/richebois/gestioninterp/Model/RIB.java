package ma.richebois.gestioninterp.Model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter
public class RIB implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "individu")
    private int individu;

    @Column(name = "rib")
    private String rib;
    @Column(name = "banque")
    private String banque;
    @Column(name = "domiciliation")
    private String domiciliation;
    @Column(name = "codeRib")
    private int codeRib;
    @Column(name = "typerib")
    private int typerib;
}
