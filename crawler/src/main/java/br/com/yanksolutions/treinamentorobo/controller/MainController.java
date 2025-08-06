package br.com.yanksolutions.treinamentorobo.controller;

import br.com.yanksolutions.treinamentorobo.flow.CapturaDados;
import br.com.yanksolutions.treinamentorobo.flow.GeradorTexto;
import br.com.yanksolutions.treinamentorobo.flow.RenomearOuExcluirFlow;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@EnableScheduling
@Controller
public class MainController {
    private final RenomearOuExcluirFlow renomearOuExcluirFlow;
    private final Logger LOGGER = LogManager.getLogger(this.getClass().getName());
    private final CapturaDados capturaDados;
    private final GeradorTexto geradorTexto;

    //@Scheduled(fixedDelay = 1000 * 60)
    @PostConstruct
    public void init() {
        try{
            LOGGER.info("#### Iniciando execução ####");
            //capturaDados.capturaDadosInit();
            geradorTexto.geradorTextoInit();
            LOGGER.info("#### Execução finalizada ####");
        } catch (Exception e) {
            LOGGER.error("Erro na execução", e);
        }
    }
}
