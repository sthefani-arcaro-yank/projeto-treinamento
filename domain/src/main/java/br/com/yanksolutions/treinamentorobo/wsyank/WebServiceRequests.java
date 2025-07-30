package br.com.yanksolutions.treinamentorobo.wsyank;


import br.com.yanksolutions.commons.utils.WaitUtils;
import br.com.yanksolutions.dmlws.DmlWs;
import br.com.yanksolutions.dmlws.exeception.WsException;


import br.com.yanksolutions.treinamentorobo.config.WsConfig;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class WebServiceRequests {
    public static final String ERRO_SELECT = "Erro ao realizar select";
    private final Logger LOGGER = LogManager.getLogger(this.getClass().getName());
    private final WsConfig wsConfig;

    public <T> T post(Object elemento, String tableName) {
        while (true) {
            try {
                return DmlWs.insertGson(elemento, tableName, wsConfig.getUser(), wsConfig.getPassword());
            } catch (Exception e) {
                LOGGER.error("Erro ao realizar o insert", e);
                WaitUtils.wait(10000);
            }
        }
    }

    public void put(Object elemento, String tableName) {

        while (true) {
            try {
                DmlWs.updateGsonExpose(elemento, tableName,  wsConfig.getUser(), wsConfig.getPassword());
                break;
            } catch (Exception e) {
                LOGGER.error("Erro ao realizar update", e);
                WaitUtils.wait(10000);
            }
        }
    }

    public JSONArray get(String query) {
        int tentivas = 5;
        while (tentivas > 0) {
            try {
                return DmlWs.select(query,  wsConfig.getUser(), wsConfig.getPassword());
            } catch (Exception e) {
                LOGGER.error(ERRO_SELECT, e);
                WaitUtils.wait(10000);
                tentivas--;
            }
        }
        throw new WsException(ERRO_SELECT);
    }

    public <T> List<T> get(String query, Class<T[]> classeArray) {
        int tentivas = 5;
        while (tentivas > 0) {
            try {
                return DmlWs.selectGson(query, classeArray,  wsConfig.getUser(), wsConfig.getPassword());
            } catch (Exception e) {
                LOGGER.error(ERRO_SELECT, e);
                WaitUtils.wait(10000);
                tentivas--;
            }
        }
        throw new WsException(ERRO_SELECT);
    }

    public <T> Optional<T> getOne(String query, Class<T> classe) {
        int tentivas = 5;
        while (tentivas > 0) {
            try {
                return DmlWs.selectOneRowGson(query, classe,  wsConfig.getUser(), wsConfig.getPassword());
            } catch (Exception e) {
                LOGGER.error(ERRO_SELECT, e);
                WaitUtils.wait(10000);
                tentivas--;
            }
        }
        throw new WsException(ERRO_SELECT);
    }


    public Optional<JSONObject> getOne(String query) {
        int tentivas = 5;
        while (tentivas > 0) {
            try {
                return DmlWs.selectOneRow(query,  wsConfig.getUser(), wsConfig.getPassword());
            } catch (Exception e) {
                LOGGER.error(ERRO_SELECT, e);
                WaitUtils.wait(10000);
                tentivas--;
            }
        }
        throw new WsException(ERRO_SELECT);
    }
}