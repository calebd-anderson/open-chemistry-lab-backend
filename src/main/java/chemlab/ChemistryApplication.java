package chemlab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.io.File;

import static chemlab.service.user.config.FileConstants.USER_FOLDER;

@SpringBootApplication
@EntityScan(basePackages = {"chemlab.model"})
@ComponentScan({"chemlab", "auth", "infrastructure", "presentation"})
@EnableMongoRepositories(basePackages = {"chemlab.repository"})
public class ChemistryApplication {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // define a BCrypt bean
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        SpringApplication.run(ChemistryApplication.class, args);
        new File(USER_FOLDER).mkdirs();
    }
}
