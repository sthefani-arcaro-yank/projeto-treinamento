package br.com.yanksolutions.treinamentorobo;

import br.com.yanksolutions.treinamentorobo.config.WsConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(WsConfig.class)
@SpringBootApplication
public class TreinamentoRoboApplication {

    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();
        SpringApplication application = new SpringApplication(TreinamentoRoboApplication.class);
        application.setAdditionalProfiles("dev");
        application.run(args);

    }

}
