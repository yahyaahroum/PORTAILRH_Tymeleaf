package ma.richebois.gestioninterp.Repository;

import ma.richebois.gestioninterp.Model.AT;
import ma.richebois.gestioninterp.Model.Certificat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificatRepository extends JpaRepository<Certificat,Long> {

    List<Certificat> findAllByAt(AT at);

    Integer countAllByAtAndTypeCertif(AT at,String type);
}
