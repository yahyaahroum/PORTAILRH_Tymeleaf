package ma.richebois.gestioninterp.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {

    @GetMapping("/test-error")
    public String testError() {
        throw new RuntimeException("Erreur de test !");
    }
}