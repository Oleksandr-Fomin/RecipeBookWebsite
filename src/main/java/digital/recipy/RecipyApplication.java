package digital.recipy;

import digital.recipy.model.Recipe;
import digital.recipy.service.RecipeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class RecipyApplication {



    public static void main(String[] args) {
        SpringApplication.run(RecipyApplication.class, args);
    }

}
