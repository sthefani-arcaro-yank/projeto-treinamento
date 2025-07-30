package br.com.yanksolutions.treinamentorobo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ws")
@Data
public class WsConfig {
    private String user;
    private String password;
}
