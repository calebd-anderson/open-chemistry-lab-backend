package chemlab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.io.File;

import static chemlab.constants.FileConstants.USER_FOLDER;

@SpringBootApplication
@EntityScan(basePackages = {"chemlab.domain.model"})
@ComponentScan({"chemlab", "auth", "infrastructure", "presentation", "repositories"})
@EnableMongoRepositories(basePackages = {"repositories", "chemlab.domain.repository"})
public class ChemistryApplication {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
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
