package ma.richebois.gestioninterp.Model;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Login extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column (unique = true)
    private String username;
    private String password;
    @Column (unique = true)
    private String email;
    private String nom;
    private String prenom;
    private String session;
    private String matchingpassword;

    @Column(name = "matricule", nullable = true)
    private int matricule;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "login_role",
            joinColumns = @JoinColumn(name = "id_login"),
            inverseJoinColumns = @JoinColumn(name = "id_role"))
    private List<Role> roles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
          name = "login_chantier",
            joinColumns = @JoinColumn(name = "id_login"),
            inverseJoinColumns = @JoinColumn(name = "id_chantier"))
    private List<Affaire> chantier;
}
