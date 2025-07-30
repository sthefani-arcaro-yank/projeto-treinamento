package br.com.yanksolutions.treinamentorobo;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TreinamentoRoboApplication {

    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();
        SpringApplication application = new SpringApplication(TreinamentoRoboApplication.class);
        application.setAdditionalProfiles("dev");
        application.run(args);

    }

}
