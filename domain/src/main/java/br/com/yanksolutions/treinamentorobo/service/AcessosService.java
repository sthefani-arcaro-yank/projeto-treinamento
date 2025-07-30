package br.com.yanksolutions.treinamentorobo.service;

import br.com.yanksolutions.treinamentorobo.config.WsConfig;
import br.com.yanksolutions.dmlws.DmlWs;
import br.com.yanksolutions.dmlws.domain.AcessoYankModel;
import lombok.AllArgsConstructor;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AcessosService {
    private final WsConfig wsConfig;

    @Value("${robo.id}")
    private String roboId;

    public AcessoYankModel getAcesso(int robo,String sistema) {
        return DmlWs.getCredential(sistema,robo,wsConfig.getUser(), wsConfig.getPassword());
    }

    public AcessoYankModel getAcesso(String sistema) {
        return DmlWs.getCredential(sistema, Integer.parseInt(roboId),wsConfig.getUser(), wsConfig.getPassword());
    }
}
