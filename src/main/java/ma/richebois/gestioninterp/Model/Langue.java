package ma.richebois.gestioninterp.Model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Langue implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String codelangue;
    private String niveau;
    private boolean lu;
    private boolean ecrit;
    private boolean parle;

   
}
