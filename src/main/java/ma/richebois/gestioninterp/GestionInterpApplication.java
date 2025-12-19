package ma.richebois.gestioninterp;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import ma.richebois.gestioninterp.Config.GlobalVariableConfig;
import ma.richebois.gestioninterp.Domaine.SecurityAuditorAware;
import ma.richebois.gestioninterp.Model.*;
import ma.richebois.gestioninterp.Repository.*;
import ma.richebois.gestioninterp.Service.AjoutServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SpringBootApplication
@EnableConfigurationProperties(GlobalVariableConfig.class)
@EnableJpaAuditing(auditorAwareRef="auditorAware")
/*@ComponentScan(basePackages = "ma.richebois.gestioninterp.Controller")*/
public class GestionInterpApplication implements CommandLineRunner {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new SecurityAuditorAware();
    }

    public static void main(String[] args) {
        SpringApplication.run(GestionInterpApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {


    }
    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        JavaTimeModule module = new JavaTimeModule();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        module.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));

        return new Jackson2ObjectMapperBuilder()
                .modules(module)
                .simpleDateFormat("yyyy-MM-dd");
    }
}
