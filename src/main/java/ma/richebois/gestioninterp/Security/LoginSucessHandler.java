package ma.richebois.gestioninterp.Security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
public class LoginSucessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String targetUrl=determineTargetUrl(authentication);
        if (response.isCommitted()){
            return;
        }
        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        redirectStrategy.sendRedirect(request,response,targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication){
        String url = "/login?error=true";

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<String>();
        for (GrantedAuthority authority : authorities){
            roles.add(authority.getAuthority());
        }

        if (roles.contains("Caissier")){
            url="/OperationCaisse";
        }else if (roles.contains("ChefProjet")){
            url="/STC/Demandes";
        }else if (roles.contains("admin")){
            url="/Acceuil";
        }else if (roles.contains("RH")){
            url="/Acceuil";
        }else if (roles.contains("Pointeur")){
            url="/Acceuil";
        }else if (roles.contains("Employé")){
        url="/Acceuil";
        }

        return url;

    }
}
