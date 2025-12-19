package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByType(String type);
}
