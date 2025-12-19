package ma.richebois.gestioninterp.Config;


import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "app.path")
public class GlobalVariableConfig {

    private String globalVariable ;


    public void setGlobalVariable(String globalVariable) {
        this.globalVariable = globalVariable;
    }
}