package ma.richebois.gestioninterp.Service;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Config.GlobalVariableConfig;
import ma.richebois.gestioninterp.Enum.CertifType;
import ma.richebois.gestioninterp.Model.AT;
import ma.richebois.gestioninterp.Model.Certificat;
import ma.richebois.gestioninterp.Repository.ATRepository;
import ma.richebois.gestioninterp.Repository.CertificatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
@AllArgsConstructor
@Service
public class CertifImpService implements CertificatService{

    private CertificatRepository certificatRepository;
    private ATRepository atRepository;
    private GlobalVariableConfig globalVariableConfig;



    @Override
    public List<Certificat> getCertifByAT(long at) {
        Optional<AT> accident = atRepository.findById(at);
        return certificatRepository.findAllByAt(accident.get());
    }

    @Override
    public Integer countCertifByType(long at,String type) {
        Optional<AT> accident = atRepository.findById(at);
        return certificatRepository.countAllByAtAndTypeCertif(accident.get(),type);
    }

    @Override
    public void saveCertif(Certificat certificat, long at, MultipartFile file) throws IOException {
        System.out.println("accident num :: :: "+at);
        Optional<AT> accident = atRepository.findById(at);
        String directory = accident.get().getIndividu().getNom().replaceAll("\\s", "") + accident.get().getIndividu().getPrenom().replaceAll("\\s", "");
        Path root = Paths.get(globalVariableConfig.getGlobalVariable() + directory);
        Path rootCertif = Paths.get(globalVariableConfig.getGlobalVariable() + directory+"/AT");

        Integer countCertifByType = certificatRepository.countAllByAtAndTypeCertif(accident.get(), CertifType.Prolongation.name());

        if (Files.notExists(root)) {
            Files.createDirectory(root);
        }
        if (Files.exists(root)){
            if (Files.notExists(rootCertif)){
                Files.createDirectory(rootCertif);
            }
        }

        if (certificat.getTypeCertif().equals(CertifType.Initial.name())){
            Files.copy(file.getInputStream(), rootCertif.resolve("Attestation_"+CertifType.Initial.name() + "_" +accident.get().getIndividu().getCodecin().trim()+"_" + directory+ ".pdf"));
            certificat.setNameCertificat("Attestation_"+CertifType.Initial.name() + "_" +accident.get().getIndividu().getCodecin().trim()+"_"+directory+".pdf");
            certificat.setAt(accident.get());

            certificatRepository.save(certificat);
        }

        if (certificat.getTypeCertif().equals(CertifType.Prolongation.name())){
            Files.copy(file.getInputStream(), rootCertif.resolve("Attestation_"+CertifType.Prolongation.name() + "_" +accident.get().getIndividu().getCodecin().trim()+"_" + directory+"_"+(countCertifByType+1) + ".pdf"));
            certificat.setNameCertificat("Attestation_"+CertifType.Prolongation.name() + "_" +accident.get().getIndividu().getCodecin().trim()+"_"+directory+"_"+(countCertifByType+1) + ".pdf");
            certificat.setAt(accident.get());

            certificatRepository.save(certificat);
        }
        if (certificat.getTypeCertif().equals(CertifType.Reprise_de_travail.name())){
            Files.copy(file.getInputStream(), rootCertif.resolve("Attestation_"+ CertifType.Reprise_de_travail.name()+ "_" +accident.get().getIndividu().getCodecin().trim()+"_" + directory+ ".pdf"));
            certificat.setNameCertificat("Attestation_"+CertifType.Reprise_de_travail.name() + "_" +accident.get().getIndividu().getCodecin().trim()+"_"+directory+"_" + ".pdf");
            certificat.setAt(accident.get());

            certificatRepository.save(certificat);
        }
        if (certificat.getTypeCertif().equals(CertifType.Guerison.name())){
            Files.copy(file.getInputStream(), rootCertif.resolve("Attestation_"+CertifType.Guerison.name() + "_" +accident.get().getIndividu().getCodecin().trim()+"_" + directory + ".pdf"));
            certificat.setNameCertificat("Attestation_"+CertifType.Guerison.name() + "_" +accident.get().getIndividu().getCodecin().trim()+"_"+directory+"_"+ ".pdf");
            certificat.setAt(accident.get());

            certificatRepository.save(certificat);
        }


    }
}
