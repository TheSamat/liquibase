package samat.learn.liquibase;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import samat.learn.liquibase.entity.Role;
import samat.learn.liquibase.model.RegisterRequest;
import samat.learn.liquibase.service.impl.AuthenticationService;

@SpringBootApplication
public class LiquibaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(LiquibaseApplication.class, args);
    }
}
