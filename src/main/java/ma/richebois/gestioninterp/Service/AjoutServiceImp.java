package ma.richebois.gestioninterp.Service;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Config.GlobalVariableConfig;
import ma.richebois.gestioninterp.Enum.IndividuState;
import ma.richebois.gestioninterp.Model.*;

import ma.richebois.gestioninterp.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
@AllArgsConstructor
@Service
public class AjoutServiceImp implements AjoutService {

    private AjoutRepository ajoutRepository;
    private ImportRepository importRepository;
    private IndividuRepository individuRepository;
    private ContratRepository contratRepository;
    private AffaireRepository affaireRepository;
    private FonctionRepository fonctionRepository;
    private UserImpService userImpService;
    private GlobalVariableConfig globalVariableConfig;




    @Override
    public List<Ajout> listImport(long id) {
        Optional<Import> imp = importRepository.findById(id);

        List<Ajout> listImport = ajoutRepository.findAllByImp(imp);
        List<Ajout> listFreshImport = new ArrayList<Ajout>();


        for (Ajout person : listImport) {
            List<Individu> ind = individuRepository.findAllByCodecinOrderByIndividuDesc(person.getCodecin());
            if (ind.size() > 0) {
                Contrat contrat = contratRepository.findAllByMatriculeOrderByNumcontratDesc(ind.get(0).getIndividu()).get(0);
                person.setExist(true);

                if (contrat.getContratactif() == 2) {
                    person.setCactif(true);
                }
                if (contrat.getSuspendu() == 2) {
                    person.setCsuspendu(true);
                }
            }
            ajoutRepository.save(person);
            listFreshImport.add(person);

        }
        return listFreshImport;
    }


    @Override
    public List<Ajout> getAll() {
        return ajoutRepository.findAll(Sort.by(Sort.Direction.ASC,"nom"));
    }

    @Override
    public Ajout getByCin(String codecin) {
        return ajoutRepository.findByCodecin(codecin);
    }

    @Override
   /* public Ajout savePerson(MultipartFile file1, MultipartFile file2, MultipartFile file3, MultipartFile file4, MultipartFile file5, MultipartFile file6, Ajout ajout, String pessai) throws IOException {

        ajout.setFanthrop(false);
        ajout.setFcnss(false);
        ajout.setFrib(false);
        ajout.setFcv(false);

        String directory = ajout.getNom().replaceAll("\\s", "") + ajout.getPrenom().replaceAll("\\s", "");

      Path root = Paths.get(globalVariableConfig.getGlobalVariable() + directory);
        System.out.println("Chemin base : " + globalVariableConfig.getGlobalVariable());
        System.out.println("Chemin complet : " + root.toString());
        if (Files.notExists(root)) {
            System.out.println("Création du dossier : " + root.toString());
            Files.createDirectories(root);
        } else {
            System.out.println("Le dossier existe déjà : " + root.toString());
        }
        Path filePath = root.resolve("CIN_Recto_" + ajout.getCodecin().trim() + "_" + ajout.getCodechantier() + directory + ".pdf");
        System.out.println("Copie de file1 vers : " + filePath);

        Files.copy(file1.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Files.copy(file1.getInputStream(), root.resolve("CIN_Recto" + "_" + ajout.getCodecin().trim() + "_" + ajout.getCodechantier() + directory + ".pdf"),StandardCopyOption.REPLACE_EXISTING);

        Files.copy(file2.getInputStream(), root.resolve("CIN_Verso" + "_" + ajout.getCodecin().trim() + "_" + ajout.getCodechantier() + directory + ".pdf"),StandardCopyOption.REPLACE_EXISTING);

        ajout.setFcin(true);

        if (file3.isEmpty()==false){
            Files.copy(file3.getInputStream(), root.resolve("Fiche_Anthropometrique" + "_" + ajout.getCodecin().trim() + "_" + ajout.getCodechantier() + directory + ".pdf"),StandardCopyOption.REPLACE_EXISTING);
            ajout.setFanthrop(true);
        }

        if (file4.isEmpty()==false){
            Files.copy(file4.getInputStream(), root.resolve("Attestation_CNSS" + "_" + ajout.getCodecin().trim() + "_" + ajout.getCodechantier() + directory + ".pdf"),StandardCopyOption.REPLACE_EXISTING);
            ajout.setFcnss(true);
        }

        if (file5.isEmpty()==false){
            Files.copy(file5.getInputStream(), root.resolve("Attestation_RIB" + "_" + ajout.getCodecin().trim() + "_" + ajout.getCodechantier() + directory + ".pdf"),StandardCopyOption.REPLACE_EXISTING);
            ajout.setFrib(true);
        }

        if (file6.isEmpty()==false){
            Files.copy(file6.getInputStream(), root.resolve("CV_" + ajout.getCodecin().trim() + "_" + ajout.getCodechantier() + directory + ".pdf"),StandardCopyOption.REPLACE_EXISTING);
            ajout.setFcv(true);
        }

        if (pessai.equals("")) {
            ajout.setPessai("15");
        } else if (pessai != "") {
            ajout.setPessai(pessai);
        }

        if (ajout.getBulletin()==null){ajout.setBulletin(false);}
        if (ajout.getWhatsapp()==null){ajout.setWhatsapp(false);}

        ajout.setModeregl("V");
        ajout.setOrigine("Saisi");
        ajout.setTypecontrat("CDD");


        ajout.setDesignation(affaireRepository.findByCode(ajout.getCodechantier()).getDesignation());


        ajout.setLibelle(fonctionRepository.findByCodefonction(ajout.getFonction()).getLibelle());
        ajout.setState(IndividuState.A_Valider.name());
        return ajoutRepository.save(ajout);
    }
*/
    public Ajout savePerson(MultipartFile file1, MultipartFile file2, MultipartFile file3,
                            MultipartFile file4, MultipartFile file5, MultipartFile file6,
                            Ajout ajout, String pessai) throws IOException {

        ajout.setFanthrop(false);
        ajout.setFcnss(false);
        ajout.setFrib(false);
        ajout.setFcv(false);

        String directory = ajout.getNom().replaceAll("\\s", "") + ajout.getPrenom().replaceAll("\\s", "");
        Path root = Paths.get(globalVariableConfig.getGlobalVariable(), directory);

        System.out.println("Chemin base : " + globalVariableConfig.getGlobalVariable());
        System.out.println("Chemin complet : " + root.toString());

        // 1. Vérification du dossier et des permissions
        if (Files.notExists(root)) {
            try {
                Files.createDirectories(root);
                System.out.println("Dossier créé : " + root.toString());
            } catch (IOException e) {
                throw new IOException("Erreur lors de la création du dossier : " + root.toString(), e);
            }
        } else {
            System.out.println("Dossier déjà existant : " + root.toString());
        }

        // Vérifie si le dossier est accessible en écriture
        if (!Files.isWritable(root)) {
            throw new IOException("Erreur : L'application n'a pas les droits d'écriture sur : " + root.toString());
        }

        // 2. Sauvegarde des fichiers
        try {
            String baseFileName = ajout.getCodecin().trim() + "_" + ajout.getCodechantier() + directory;

            if (saveIfPresent(file1, root, "CIN_Recto_" + baseFileName + ".pdf") != null
                    && saveIfPresent(file2, root, "CIN_Verso_" + baseFileName + ".pdf") != null) {
                ajout.setFcin(true);
            }

            if (saveIfPresent(file3, root, "Fiche_Anthropometrique_" + baseFileName + ".pdf") != null)
                ajout.setFanthrop(true);

            if (saveIfPresent(file4, root, "Attestation_CNSS_" + baseFileName + ".pdf") != null)
                ajout.setFcnss(true);

            if (saveIfPresent(file5, root, "Attestation_RIB_" + baseFileName + ".pdf") != null)
                ajout.setFrib(true);

            if (saveIfPresent(file6, root, "CV_" + baseFileName + ".pdf") != null)
                ajout.setFcv(true);
        } catch (IOException e) {
            throw new IOException("Erreur lors de la copie des fichiers dans le dossier : " + root.toString(), e);
        }

        // 3. Données supplémentaires
        ajout.setPessai((pessai == null || pessai.isEmpty()) ? "15" : pessai);
        ajout.setBulletin(ajout.getBulletin() != null ? ajout.getBulletin() : false);
        ajout.setWhatsapp(ajout.getWhatsapp() != null ? ajout.getWhatsapp() : false);
        ajout.setModeregl("V");
        ajout.setOrigine("Saisi");
        ajout.setTypecontrat("CDD");

        // 4. Récupération des désignations et libellés
        ajout.setDesignation(affaireRepository.findByCode(ajout.getCodechantier()).getDesignation());
        ajout.setLibelle(fonctionRepository.findByCodefonction(ajout.getFonction()).getLibelle());
        ajout.setState(IndividuState.A_Valider.name());

        // 5. Sauvegarde finale
        return ajoutRepository.save(ajout);
    }




    @Override
    public Ajout updatePerson(MultipartFile file1, MultipartFile file2, MultipartFile file3, MultipartFile file4, MultipartFile file5, MultipartFile file6, Ajout ajout) throws IOException {

        ajout.setFcin(false);
        ajout.setFanthrop(false);
        ajout.setFcnss(false);
        ajout.setFrib(false);
        ajout.setFcv(false);

        String directory = ajout.getNom().replaceAll("\\s","") + ajout.getPrenom().replaceAll("\\s","");
        Path root = Paths.get(globalVariableConfig.getGlobalVariable(), directory);
        Files.createDirectories(root);

        ajout.setFcin(false);
        ajout.setFanthrop(false);
        ajout.setFcnss(false);
        ajout.setFrib(false);
        ajout.setFcv(false);

        if (saveIfPresent(file1, root, "CIN_Recto_" + ajout.getCodecin().trim() + "_" + ajout.getCodechantier() + directory + ".pdf") != null)
            ajout.setFcin(true);

        if (saveIfPresent(file2, root, "CIN_Verso_" + ajout.getCodecin().trim() + "_" + ajout.getCodechantier() + directory + ".pdf") != null)
            ajout.setFcin(true);

        if (saveIfPresent(file3, root, "Fiche_Anthropometrique_" + ajout.getCodecin().trim() + "_" + ajout.getCodechantier() + directory + ".pdf") != null)
            ajout.setFanthrop(true);

        if (saveIfPresent(file4, root, "Attestation_CNSS_" + ajout.getCodecin().trim() + "_" + ajout.getCodechantier() + directory + ".pdf") != null)
            ajout.setFcnss(true);

        if (saveIfPresent(file5, root, "Attestation_RIB_" + ajout.getCodecin().trim() + "_" + ajout.getCodechantier() + directory + ".pdf") != null)
            ajout.setFrib(true);

        if (saveIfPresent(file6, root, "CV_" + ajout.getCodecin().trim() + "_" + ajout.getCodechantier() + directory + ".pdf") != null)
            ajout.setFcv(true);

        ajout.setModeregl("V");
        ajout.setOrigine("Saisi");
        ajout.setTypecontrat("CDD");
        ajout.setDesignation(affaireRepository.findByCode(ajout.getCodechantier()).getDesignation());
        ajout.setLibelle(fonctionRepository.findByCodefonction(ajout.getFonction()).getLibelle());
        ajout.setState(IndividuState.A_Valider.name());

        return ajoutRepository.save(ajout);
    }

    @Override
    public Page<Ajout> findPaginatedAjout(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Ajout> listCons;

        if (getAll().size() < startItem) {
            listCons = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, getAll().size());
            listCons = getAll().subList(startItem, toIndex);
        }
        Page<Ajout> consultPage = new PageImpl<Ajout>(listCons, PageRequest.of(currentPage, pageSize), getAll().size());


        return consultPage;
    }


    @Override
    public Ajout valider(long id, String salaire, String pessai) {

        Optional<Ajout> ajout = ajoutRepository.findById(id);
        int lastMatricule = getLastMAtricul(ajout.get());

        ajout.get().setMatricule(lastMatricule);
        ajout.get().setState(IndividuState.Validé.name());

        ajout.get().setSalaire(Double.parseDouble(salaire));
        ajout.get().setPessai(pessai);
        ajoutRepository.save(ajout.get());

        return ajout.get();
    }

    @Override
    public Ajout rejeter(long id,String motifRejet) {
        Optional<Ajout> ajout = ajoutRepository.findById(id);
        ajout.get().setState(IndividuState.Rejeté.name());
        ajout.get().setMotifRejet(motifRejet);
        ajoutRepository.save(ajout.get());
        return null;
    }

    @Override
    public Ajout findById(long id) {
        Optional<Ajout> p = ajoutRepository.findById(id);
        return p.get();
    }


    @Override
    public Boolean deletePerson(Long id) {
        ajoutRepository.deleteById(id);

        return true;
    }


    @Override
    public Integer getLastMAtricul(Ajout ajout) {

        List<String> states = new ArrayList<String>();
        states.add(IndividuState.Validé.name());
        states.add(IndividuState.A_Imprimer.name());
        states.add(IndividuState.Imprimé.name());

        List<Individu> individu = individuRepository.findAllByCodecinOrderByIndividuDesc(ajout.getCodecin());

        if (individu.isEmpty()){
            List<Ajout> ajoutList = ajoutRepository.findAllByStateInOrderByMatriculeDesc(states);

            int lastmatriculeajout = ajoutList.get(0).getMatricule();

            List<Individu> individuList = individuRepository.findAll(Sort.by(Sort.Direction.DESC,"individu"));

            int lastIndividu = individuList.get(0).getIndividu();

            if (lastmatriculeajout != 0 && lastmatriculeajout > lastIndividu) {
                return lastmatriculeajout + 1;
            } else
                return lastIndividu + 1;
        }else
            return individu.get(0).getIndividu();






    }

    @Override
    public List<Ajout> getPersonnesSaisie(String start,String end) throws ParseException {
        Date datestart = new SimpleDateFormat("yyyy-MM-dd").parse(start);
        Date dateend = new SimpleDateFormat("yyyy-MM-dd").parse(end);
        List<String> states = new ArrayList<>();
        states.add(IndividuState.Validé.name());
        states.add(IndividuState.A_Imprimer.name());
        states.add(IndividuState.Imprimé.name());
        List<String> origine = new ArrayList<>();
        origine.add("Saisi");
        List<Ajout> ajoutSaisie = ajoutRepository.findAllByStateInAndOrigineInAndDateentreeBetweenOrderByMatriculeAsc(states,origine,datestart,dateend);

        return ajoutSaisie;
    }



    @Override
    public Ajout personneContractUpload(Long id, MultipartFile file1, MultipartFile file2, MultipartFile file3, Optional<Ajout> ajout) throws IOException {

        ajout = ajoutRepository.findById(id);
        String directory = ajout.get().getNom().trim().replaceAll("\\s","") + ajout.get().getPrenom().trim().replaceAll("\\s","");
        Path root = Paths.get(globalVariableConfig.getGlobalVariable(), directory);
        Files.createDirectories(root);

        saveIfPresent(file1, root, "Contrat_" + ajout.get().getCodecin().trim() + "_" + ajout.get().getCodechantier() + ".pdf");
        saveIfPresent(file2, root, "Contrat_Decision_" + ajout.get().getCodecin().trim() + "_" + ajout.get().getCodechantier() + ".pdf");
        saveIfPresent(file3, root, "Contrat_Decision_Equip_" + ajout.get().getCodecin().trim() + "_" + ajout.get().getCodechantier() + ".pdf");

        ajout.get().setState(IndividuState.Imprimé.name());
        return ajoutRepository.save(ajout.get());

    }

    @Override
    public Individu getIndividu(Long id) {
        Optional<Individu> individu = individuRepository.findById(id);

        return individu.get();
    }

    @Override
    public List<Ajout> getPersonByState(String state) {
        Login login = userImpService.findbyusername();
        List<Role> roles = login.getRoles();
        List<String> codeChantier = new ArrayList<String>();
        List<Affaire> affaireList = login.getChantier();
        List<Ajout> ajoutList = new ArrayList<>();

        for (Role role : roles){
            if (role.getType().equals("Pointeur"))
            {
                for (Affaire chantier:affaireList)
                {
                    codeChantier.add(chantier.getCode());
                    if (state.equals("All"))
                    {
                        List<Ajout> ajoutList1 = ajoutRepository.findAllByCodechantierInAndOrigineOrderByNomAsc(codeChantier,"Saisi");
                        ajoutList.addAll(ajoutList1);
                    }
                    if (!state.equals("All"))
                    {
                        List<Ajout> listPerStateAndAffaire = ajoutRepository.findAllByCodechantierInAndOrigineAndStateOrderByNom(codeChantier,"Saisi",state);
                        ajoutList.addAll(listPerStateAndAffaire);
                    }

                }
                return ajoutList;
            }
            else if (state.equals("All"))
            {
               return ajoutRepository.findAllByOrigineOrderByNomAsc("Saisi");
            }
            if (!state.equals("All"))
            {
                return ajoutRepository.findAllByStateAndOrigineOrderByNomAsc(state,"Saisi");
            }

        }
        return null;
    }
    private Path saveIfPresent(MultipartFile file, Path dir, String filename) throws IOException {
        if (file == null || file.isEmpty()) return null;
        Files.createDirectories(dir);
        Path dest = dir.resolve(filename);
        try (java.io.InputStream in = file.getInputStream()) {
            Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
        }
        return dest;
    }

    @Override
    public Page<Ajout> findPaginatedAjoutByState(Pageable pageable, String state) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Ajout> listCons;

        if (getPersonByState(state).size() < startItem) {
            listCons = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, getPersonByState(state).size());
            listCons = getPersonByState(state).subList(startItem, toIndex);
        }
        Page<Ajout> consultPage = new PageImpl<Ajout>(listCons, PageRequest.of(currentPage, pageSize), getPersonByState(state).size());


        return consultPage;
    }
    @Override
    public Page<Ajout> AjoutPage(Pageable pageable, List<Ajout> ajoutList)
    {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Ajout> listAjout;
        if (ajoutList.size() < startItem) {
            listAjout = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, ajoutList.size());
            listAjout = ajoutList.subList(startItem, toIndex);
        }
        Page<Ajout> ajoutPage = new PageImpl<Ajout>(listAjout, PageRequest.of(currentPage, pageSize), ajoutList.size());
        return ajoutPage;
    }
    
    


}
