package ma.richebois.gestioninterp.Service;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Domaine.MyConstants;
import ma.richebois.gestioninterp.Model.*;
import ma.richebois.gestioninterp.Repository.LoginRepository;
import ma.richebois.gestioninterp.Repository.RoleRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@AllArgsConstructor
@Service
public class UserImpService implements UserService{

    private LoginRepository loginRepository;
    private RoleRepository roleRepository;
    public JavaMailSender emailSender;


    @Override
    public Login saveUser(Login login) {
    	
        String password = login.getPassword();
        //System.out.println("Password saisi au niveau du fourmulaire: "+login.getPassword());
        password = BCrypt.hashpw(password, BCrypt.gensalt());

        login.setPassword(password);

        return loginRepository.save(login);
    }

    @Override
    public List<Login> getAllUsers() {
        return loginRepository.findAll();
    }

    @Override
    public Login finduserById(Long id) {

        Optional<Login> user = loginRepository.findById(id);
        return user.get();
    }

    @Override
    public Boolean deleteUser(Long id) {
        loginRepository.deleteById(id);
        return true;
    }

    @Override
    public Login findbyusername() {
        Authentication aut = SecurityContextHolder.getContext().getAuthentication();
        String username = aut.getName();
        Login login = loginRepository.findByUsernameAndSession(username,"active");
        return login;
    }

    @Override
    public List<Login> finuserbyrole(List<Role> roles) {
        List<Login> rhusers = loginRepository.findAllByRolesIn(roles);
        return rhusers;
    }

    @Override
    public void sendEmail(Ajout ajout) {
        DateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
        Login login = findbyusername();
        Role role = roleRepository.findByType("RH");
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        List<Login> rhusers = finuserbyrole(roles);

        for (Login user : rhusers) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(MyConstants.MY_EMAIL);
            message.setTo(user.getEmail());
            message.setSubject("Demande de Validation d'un employé ajouté par l'application PortailRH");
            message.setText("Bonjour \n \n " +
                    "       Vous avez un contrat soumis pour validation (voir les informations ci-dessous) par le responsable du pointage :"+login.getNom().trim().toUpperCase()+" "+login.getPrenom().trim().toUpperCase()+"."+" \n - Nom complet :" + ajout.getNom().trim().toUpperCase() + " " + ajout.getPrenom().trim().toUpperCase() + "\n - code CIN :" + ajout.getCodecin().trim().toUpperCase()+".\n - le nom chantier : "+ajout.getDesignation().trim().toUpperCase()+".\n - la date d'embauche : "+sourceFormat.format(ajout.getDateentree())+" . \n\n"
                        +"Sincéres salutations");
            this.emailSender.send(message);
        }
    }

    @Override
    public void sendEmailSTC(STC stc) {
        DateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
        Role role = roleRepository.findByType("RH");
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        List<Login> rhusers = finuserbyrole(roles);

        for (Login user : rhusers) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(MyConstants.MY_EMAIL);
            message.setTo(user.getEmail());
            message.setSubject("Validation du solde de tout compte");
            message.setText("Bonjour \n \n " +
                    "       Merci de valider le solde de tout compte du :"+stc.getIndividu().getNom().trim().toUpperCase()+" "+stc.getIndividu().getPrenom().trim().toUpperCase()+" préparé par " +stc.getCreatedBy().trim().toUpperCase()+
                    " le pointeur du projet "+stc.getAffaire().getCode().trim().toUpperCase()+" "+stc.getAffaire().getDesignation().trim().toUpperCase()+" le "+sourceFormat.format(stc.getDatedepart())+" . \n\n"
                    +"Sincéres salutations");
            this.emailSender.send(message);
        }
    }

    @Override
    public void sendEmailCHPRSTC(STC stc) {
        DateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
        Login userLogin = findbyusername();
        Role role = roleRepository.findByType("ChefProjet");
        List<Role> roles = new ArrayList<>();
        roles.add(role);

        Role roleRH = roleRepository.findByType("RH");
        List<Role> rolesRH = new ArrayList<>();
        roles.add(roleRH);
        List<Login> rhusers = finuserbyrole(rolesRH);

        Login login = loginRepository.findByChantierAndRolesIn(stc.getAffaire(), roles);

        if (login==null){

        }else {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(MyConstants.MY_EMAIL);
            message.setTo(login.getEmail());
            message.setSubject("Validation du STC");

            for (Login user : rhusers) {
                message.setCc(user.getEmail());
            }
            message.setText("Bonjour \n \n " +
                    "       Merci de valider le solde de tout compte du :" + stc.getIndividu().getNom().trim().toUpperCase() + " " + stc.getIndividu().getPrenom().trim().toUpperCase() + " préparé par " + userLogin.getNom().trim().toUpperCase()+" "+userLogin.getPrenom().trim().toUpperCase() +
                    " le poiteur du projet " + stc.getAffaire().getCode().trim().toUpperCase() + " " + stc.getAffaire().getDesignation().trim().toUpperCase() + " le " + sourceFormat.format(stc.getDatedepart()) + " . \n\n"
                    + "Sincéres salutations");
            this.emailSender.send(message);
        }
    }




}
