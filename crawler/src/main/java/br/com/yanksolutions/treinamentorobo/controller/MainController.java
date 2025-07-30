package br.com.yanksolutions.treinamentorobo.controller;

import br.com.yanksolutions.treinamentorobo.flow.RenomearOuExcluirFlow;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@EnableScheduling
@Controller
public class MainController {
    private final RenomearOuExcluirFlow renomearOuExcluirFlow;
    private final Logger LOGGER = LogManager.getLogger(this.getClass().getName());

    @Scheduled(fixedDelay = 1000 * 60)
    public void init() {
        try{
            LOGGER.info("#### Iniciando execução ####");
            renomearOuExcluirFlow.seleniumExemploUndetected();
//            renomearOuExcluirFlow.seleniumExemplo();
//            renomearOuExcluirFlow.seleniumExemploVpn();
            LOGGER.info("#### Execução finalizada ####");
        } catch (Exception e) {
            LOGGER.error("Erro na execução", e);
        }
    }
}
