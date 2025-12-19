package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Affaire;
import ma.richebois.gestioninterp.Model.Login;
import ma.richebois.gestioninterp.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginRepository extends JpaRepository<Login,Long> {

    Login findByUsernameAndSession(String username,String session);

    List<Login> findAllByRolesIn(List<Role> roles);

    Login findByChantierAndRolesIn(Affaire chantier,List<Role> roles);
}
