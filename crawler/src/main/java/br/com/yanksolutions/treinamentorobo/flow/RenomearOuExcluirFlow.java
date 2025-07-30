package br.com.yanksolutions.treinamentorobo.flow;

import br.com.yanksolutions.treinamentorobo.enums.StatusExecEnum;
import br.com.yanksolutions.treinamentorobo.model.RenomearOuExcluirModel;
import br.com.yanksolutions.treinamentorobo.service.AcessosService;
import br.com.yanksolutions.treinamentorobo.service.RenomearOuExcluirService;
import br.com.yanksolutions.treinamentorobo.utils.selenium.config.WebDriverConfig;
import br.com.yanksolutions.commons.utils.serilization.GsonSerialization;
import br.com.yanksolutions.treinamentorobo.healthcheck.HealthCheckUtils;
import br.com.yanksolutions.treinamentorobo.utils.selenium.SeleniumUtils;
import br.com.yanksolutions.dmlws.domain.AcessoYankModel;
import lombok.RequiredArgsConstructor;
import kong.unirest.json.JSONArray;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RenomearOuExcluirFlow {
    private static final String ERRO_FLUXO = "Erro ao realizar o teste";

    private final Logger LOGGER = LogManager.getLogger(this.getClass().getName());
    private final RenomearOuExcluirService renomearOuExcluirService;
    private final SeleniumUtils seleniumUtils;
    private final AcessosService acessosService;
    private final HealthCheckUtils healthCheckUtils = HealthCheckUtils.getInstance();

    public void seleniumExemploUndetected() {
        try {
            LOGGER.info("### Iniciando Fluxo Selenium Undetected ###");

            seleniumUtils.initDriver(
                    WebDriverConfig
                            .builder()
                            .headless(false)
                            .undetected(true)
                            .pathDownload("downloads")
                            .build()
            );

            seleniumUtils.openUrl("https://www.w3schools.com/html/html_tables.asp");

            JSONArray customersJSONArray = seleniumUtils.parseHtmlToTableToJsonByXpath("//*[@id=\"customers\"]");

            List<RenomearOuExcluirModel> renomearOuExcluirModels = GsonSerialization.convertJsonToList(GsonSerialization.GSON_DATES, customersJSONArray.toString(), RenomearOuExcluirModel[].class);

            int count = 1;
            for (RenomearOuExcluirModel renomearOuExcluirModel : renomearOuExcluirModels) {
                LOGGER.info("Registro " + count++ + "/" + renomearOuExcluirModels.size());
                LOGGER.info("Contato: " + renomearOuExcluirModel.getContact());

                if (renomearOuExcluirService.isPresent(renomearOuExcluirModel.getContact())) {
                    LOGGER.info("Registro já existe");
                    continue;
                }

                LOGGER.info("Empresa: " + renomearOuExcluirModel.getCompany());
                LOGGER.info("País: " + renomearOuExcluirModel.getCountry());
                renomearOuExcluirModel.setStatusExec(StatusExecEnum.INSERIDO);
                renomearOuExcluirService.insert(renomearOuExcluirModel);
            }

            LOGGER.info("###  Fluxo Selenium Undetected finalizado com sucesso ###");
            healthCheckUtils.setStatus(true);
        } catch (Exception e) {
            LOGGER.error(ERRO_FLUXO, e);
            healthCheckUtils.setStatus(false);
            healthCheckUtils.sendPing(ERRO_FLUXO, e);
        } finally {
            seleniumUtils.closeDriver();
        }
    }


    public void seleniumExemplo() {
        try {
            LOGGER.info("###  Iniciando Fluxo Selenium ###");

            List<RenomearOuExcluirModel> renomearOuExcluirModels = renomearOuExcluirService.findByCodStatus(StatusExecEnum.INSERIDO);

            if (CollectionUtils.isEmpty(renomearOuExcluirModels)) {
                LOGGER.info("Não há registros para serem processados");
                return;
            }

            seleniumUtils.initDriver(
                    WebDriverConfig
                            .builder()
                            .headless(false)
                            .closeWithPids(true)
                            .build()
            );

            seleniumUtils.openUrl("https://geradornv.com.br/gerador-telefone/");
            seleniumUtils.waitClickableByXPath("//*[@id=\"nv-new-generator-telephone\"]", 30);

            int count = 1;
            for (RenomearOuExcluirModel renomearOuExcluirModel : renomearOuExcluirModels) {
                try {
                    LOGGER.info(String.format(" %d/%d Capturando telefone id %d", count++, renomearOuExcluirModels.size(), renomearOuExcluirModel.getId()));
                    renomearOuExcluirModel.setStatusExec(StatusExecEnum.CAPTURANDO_TELEFONE);

                    renomearOuExcluirService.update(renomearOuExcluirModel);

                    seleniumUtils.clickByXpath("//*[@id=\"nv-new-generator-telephone\"]");
                    String telefone = seleniumUtils.getTextByXPath("//*[@id=\"nv-field-generator-telephone\"]");
                    LOGGER.info("Telefone: " + telefone);

                    renomearOuExcluirModel.setTelefone(telefone);
                    renomearOuExcluirModel.setStatusExec(StatusExecEnum.TELEFONE_CAPTURADO);
                } catch (Exception e) {
                    renomearOuExcluirModel.setStatusExec(StatusExecEnum.ERRO_TELEFONE);
                    LOGGER.error("Erro ao capturar o telefone", e);
                }

                renomearOuExcluirService.update(renomearOuExcluirModel);
            }

            seleniumUtils.openUrl("https://geradornv.com.br/gerador-rg-sp/");
            seleniumUtils.waitClickableByXPath("//*[@id=\"nv-new-generator-rg-sp\"]", 30);

            count = 1;
            for (RenomearOuExcluirModel renomearOuExcluirModel : renomearOuExcluirModels) {
                try {
                    LOGGER.info(String.format(" %d/%d Capturando rg id %d", count++, renomearOuExcluirModels.size(), renomearOuExcluirModel.getId()));
                    renomearOuExcluirModel.setStatusExec(StatusExecEnum.CAPTURANDO_RG);

                    renomearOuExcluirService.update(renomearOuExcluirModel);

                    seleniumUtils.clickByXpath("//*[@id=\"nv-new-generator-rg-sp\"]");
                    String telefone = seleniumUtils.getTextByXPath("//*[@id=\"nv-field-generator-rg-sp\"]");
                    LOGGER.info("Telefone: " + telefone);

                    renomearOuExcluirModel.setTelefone(telefone);
                    renomearOuExcluirModel.setStatusExec(StatusExecEnum.SUCESSO);
                } catch (Exception e) {
                    renomearOuExcluirModel.setStatusExec(StatusExecEnum.ERRO_RG);
                    LOGGER.error("Erro ao capturar o RG", e);
                }
                renomearOuExcluirService.update(renomearOuExcluirModel);
            }


            LOGGER.info("###  Fluxo Selenium finalizado com sucesso ###");
            healthCheckUtils.setStatus(true);
        } catch (Exception e) {
            LOGGER.error(ERRO_FLUXO, e);
            healthCheckUtils.setStatus(false);
            healthCheckUtils.sendPing(ERRO_FLUXO, e);
        } finally {
            seleniumUtils.closeDriver();
        }
    }


    public void seleniumExemploVpn() {
        try {
            LOGGER.info("### Iniciando Fluxo Selenium Vpn ###");

            // É possível adicionar a Proton VPN na lista de extensões, dessa forma não é preciso utilizar o Chrome local.
            // Para fazer isso, é necessário baixar o código-fonte dela utilizando https://chromewebstore.google.com/detail/extension-source-download/dlbdalfhhfecaekoakmanjflmdhmgpea.
            // Basta adicionar em pathExtensoes diretamente na Classe WebDriverConfig ou na hora de criar o WebDriverConfig. Obs: O mesmo vale pra as extensões de captcha

            seleniumUtils.initDriver(
                    WebDriverConfig
                            .builder()
                            .headless(false)
                            .undetected(true)
                            .localChromeProfile(true)
                            .build()
            );

            AcessoYankModel acessosProton = acessosService.getAcesso(997, "acesso1");
            seleniumUtils.connectToVnp(acessosProton.getUsuario(), acessosProton.getSenha());
            seleniumUtils.openUrl("https://nopecha.com/captcha");
            seleniumUtils.disconnectVnp();
            seleniumUtils.connectToVnp(acessosProton.getUsuario(), acessosProton.getSenha());

            LOGGER.info("###  Fluxo Selenium Vpn finalizado com sucesso ###");
            healthCheckUtils.setStatus(true);
        } catch (Exception e) {
            LOGGER.error(ERRO_FLUXO, e);
            healthCheckUtils.setStatus(false);
            healthCheckUtils.sendPing(ERRO_FLUXO, e);
        } finally {
            seleniumUtils.closeDriver();
        }
    }

}
