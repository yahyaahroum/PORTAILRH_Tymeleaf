package ma.richebois.gestioninterp.Model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.Valid;

import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="Sanction")
public class Sanction extends Auditable<String> implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateSanction;
    @Column
    private long duree;
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateApplication;
    @Column
    private String typeSanction;
    @Column
    private String motif;
    @Column
    private boolean valide;
    @Column
    private boolean imprime;
    @ManyToOne
    @JoinColumn(name="id_login")
    private Login login;
    @ManyToOne
    @JoinColumn(name="id_individu")
    private Individu individu;

    public boolean getValid(){
      return this.valide;
    }
    public boolean getImprime(){
        return this.imprime;
    }


}
