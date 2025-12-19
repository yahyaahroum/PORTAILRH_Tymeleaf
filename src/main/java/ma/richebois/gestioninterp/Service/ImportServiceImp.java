package ma.richebois.gestioninterp.Service;

import ma.richebois.gestioninterp.Model.Import;
import ma.richebois.gestioninterp.Repository.ImportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImportServiceImp implements ImportService{

    @Autowired
    private ImportRepository importRepository;

    @Override
    public Import saveImport(Import imp) {
        return importRepository.save(imp);
    }
}
