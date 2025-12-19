package ma.richebois.gestioninterp.Service;

import ma.richebois.gestioninterp.Model.*;

import java.util.List;

public interface UserService {

    public Login saveUser(Login login);
    public List<Login> getAllUsers();
    public Login finduserById(Long id);
    public Boolean deleteUser(Long id);
    public Login findbyusername();
    public List<Login> finuserbyrole(List<Role> roles);

    public void sendEmail(Ajout ajout);
    public void sendEmailSTC(STC stc);
    public void sendEmailCHPRSTC(STC stc);


}
