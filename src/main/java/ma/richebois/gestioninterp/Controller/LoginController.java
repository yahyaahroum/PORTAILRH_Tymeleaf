package ma.richebois.gestioninterp.Controller;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Model.Affaire;
import ma.richebois.gestioninterp.Model.Login;
import ma.richebois.gestioninterp.Model.Role;
import ma.richebois.gestioninterp.Repository.AffaireRepository;
import ma.richebois.gestioninterp.Repository.RoleRepository;
import ma.richebois.gestioninterp.Service.UserImpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
@AllArgsConstructor
@Controller
public class LoginController {

    private UserImpService userImpService;
    private RoleRepository roleRepository;
    private AffaireRepository affaireRepository;

    @GetMapping("/Utilisateurs/Ajouter")
    @PreAuthorize("hasAnyAuthority('admin')")
    public String getAddUserPage(Model model){
        model.addAttribute("roles",roleRepository.findAll());
        model.addAttribute("chantier",affaireRepository.findAll());
        return "Administration/AddLogin";
    }

    @PostMapping("/Utilisateurs/Ajouter")
    @PreAuthorize("hasAnyAuthority('admin')")
    public String AddUser(Login login){
        userImpService.saveUser(login);
        return "redirect:/Utilisateurs/Liste";
    }
    @GetMapping("/Utilisateurs/Liste")
    @PreAuthorize("hasAnyAuthority('admin')")
    public String getAllUsers(Model model){
        model.addAttribute("users",userImpService.getAllUsers());
        return "Administration/ListUser";
    }

    @GetMapping("/Utilisateurs/Editer/{id}")
    @PreAuthorize("hasAnyAuthority('admin')")
    public String getLogin(Model model, @PathVariable("id") Long id){

        List<Login> users = userImpService.getAllUsers();
        List<Role> roles = roleRepository.findAll();
        List<Affaire> chantier = affaireRepository.findAll();

        Login user = null;

        for (Login u : users){
            if (u.getId()==id){
                user=u;
            }
        }
        model.addAttribute("user",user);
        model.addAttribute("roles",roles);
        model.addAttribute("chantier",chantier);

        return "Administration/EditLogin";
    }

    @PostMapping("/Utilisateurs/Editer/{id}")
    @PreAuthorize("hasAnyAuthority('admin')")
    public String updateLogin(@PathVariable("id") Long id,Login login,Model model){
        List<Login> users = userImpService.getAllUsers();
        List<Role> roles = roleRepository.findAll();
        List<Affaire> chantier = affaireRepository.findAll();

        Login user = null;

        for (Login u : users){
            if (u.getId()==id){
                user=u;
            }
        }
        model.addAttribute("user",user);
        model.addAttribute("roles",roles);
        model.addAttribute("chantier",chantier);
        userImpService.saveUser(login);
        return "redirect:/Utilisateurs/Liste";
    }

    @GetMapping("/Utilisateurs/Supprimer/{id}")
    @PreAuthorize("hasAnyAuthority('admin')")
    public String deleteUser(@PathVariable("id") Long id){
        userImpService.deleteUser(id);
        return "redirect:/Utilisateurs/Liste";
    }

}
