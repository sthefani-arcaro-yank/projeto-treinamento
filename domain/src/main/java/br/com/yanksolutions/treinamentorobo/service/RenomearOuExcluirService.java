package br.com.yanksolutions.treinamentorobo.service;


import br.com.yanksolutions.treinamentorobo.enums.*;
import br.com.yanksolutions.treinamentorobo.model.RenomearOuExcluirModel;
import br.com.yanksolutions.treinamentorobo.wsyank.WebServiceRequests;
import kong.unirest.json.JSONObject;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RenomearOuExcluirService {
    private final WebServiceRequests webServiceRequests;
    private final Logger LOGGER = LogManager.getLogger(this.getClass().getName());

    @Value("${robo.tabela}")
    private String tabela;

    public List<RenomearOuExcluirModel> findByCodStatus(StatusExecEnum statusExecEnum) {
        return new ArrayList<>(
                webServiceRequests.get(String.format("select * from %s where cod_status = %d", tabela, statusExecEnum.getCodStatus()), RenomearOuExcluirModel[].class)
        );
    }

    public boolean isPresent(String contact) {
        Optional<JSONObject> optionalResult = webServiceRequests.getOne(String.format("select 1 from %s where CONTACT = '%s' limit 1", tabela, contact));
        return optionalResult.isPresent();
    }


    public long countByCodStatus(StatusExecEnum statusExecEnum) {
        Optional<JSONObject> optionalResult = webServiceRequests.getOne(String.format("select count(1) qtd from %s where cod_status = %d", tabela, statusExecEnum.getCodStatus()));
        return optionalResult.map(jsonObject -> jsonObject.getLong("qtd")).orElse(0L);
    }

    public void update(RenomearOuExcluirModel renomearOuExcluirModel) {
        renomearOuExcluirModel.setTimeExec(LocalDateTime.now());

        if (renomearOuExcluirModel.getPorcentExec() == 100) {
            renomearOuExcluirModel.setStatus(1);
        } else if (renomearOuExcluirModel.getPorcentExec() == 1)
            renomearOuExcluirModel.setTimeBegin(LocalDateTime.now());

        LOGGER.info(
                String.format("Atualizando registro %d - Porcentagem: %d - Status: %d",
                        renomearOuExcluirModel.getId(),
                        renomearOuExcluirModel.getPorcentExec(),
                        renomearOuExcluirModel.getStatus()
                )
        );

        webServiceRequests.put(renomearOuExcluirModel, tabela);
    }

    public RenomearOuExcluirModel insert(RenomearOuExcluirModel renomearOuExcluirModel) {
        renomearOuExcluirModel.setTimeImport(LocalDateTime.now());

        renomearOuExcluirModel = webServiceRequests.post(renomearOuExcluirModel, tabela);
        LOGGER.info(String.format("ID Gerado: %d", renomearOuExcluirModel.getId()));

        return renomearOuExcluirModel;
    }


}
