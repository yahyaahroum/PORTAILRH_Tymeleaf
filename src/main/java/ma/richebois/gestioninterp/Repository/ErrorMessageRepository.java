package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.ErrorMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.logging.ErrorManager;

@Repository
public interface ErrorMessageRepository extends JpaRepository<ErrorMessage,Long> {
}
