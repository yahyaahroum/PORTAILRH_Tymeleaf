package ma.richebois.gestioninterp.Service;

import lombok.AllArgsConstructor;
import ma.richebois.gestioninterp.Model.Login;
import ma.richebois.gestioninterp.Model.Role;
import ma.richebois.gestioninterp.Repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
@AllArgsConstructor
@Service
public class LoginImpService implements LoginService{

    private LoginRepository loginRepository;
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Login login= loginRepository.findByUsernameAndSession(username,"active");
        if (login == null){
            throw new UsernameNotFoundException("Nom d'utilisateur introuvable");
        }
        return new User(login.getUsername(), login.getPassword(), getAuthorities(login.getRoles()));

    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
        return getGrantedAuthorities(roles);
    }

    private List<GrantedAuthority> getGrantedAuthorities(Collection<Role> roles) {
        return roles.stream().map(role ->
                new SimpleGrantedAuthority(role.getType())
        ).collect(Collectors.toList());
    }


}
