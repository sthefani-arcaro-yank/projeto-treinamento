package br.com.yanksolutions.treinamentorobo.healthcheck;


import br.com.yanksolutions.healthcheck.request.Healthcheck;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;


@Component

public class HealthCheckUtils extends HealthCheckStatus {
    private final Logger LOGGER = LogManager.getLogger(this.getClass().getName());
    private static final String ROBO_ID = "PING_HASH_SUBSTITUIR";
    private static HealthCheckUtils instance;

    private HealthCheckUtils() {
    }

    public static synchronized HealthCheckUtils getInstance() {
        if (instance == null) {
            instance = new HealthCheckUtils();
        }
        return instance;
    }


    public void sendPing(String text) {
        try {
            Healthcheck.sendPing(getStatus(), ROBO_ID, text);
        } catch (Exception e) {
            LOGGER.error("Erro ao gravar ping de sucesso no Health Check", e);
        }
    }


    public void sendPing(String text, Exception exception) {
        try {
            Healthcheck.sendPing(getStatus(), ROBO_ID, text + ":" + ExceptionUtils.getStackTrace(exception));
        } catch (Exception e) {
            LOGGER.error("Erro ao gravar ping de sucesso no Health Check", e);
        }
    }

    public void updateSchedule(String cron, int grace) {
        try {
            Healthcheck.updateScheduleCron("SQUAD_ALTERAR", ROBO_ID, cron, grace);
            sendPing("Schedule atualizada para " + cron);
        } catch (Exception e) {
            LOGGER.error("Erro ao atualizar o Schedule no Health Check", e);
        }
    }

    public void updateFixedRate(int rate, int grace) {
        try {
            Healthcheck.updateScheduleFixedRate("SQUAD_SUBSTITUIR", ROBO_ID, rate, grace);
            sendPing("Schedule atualizada para cada " + rate + " segundos");
        } catch (Exception e) {
            LOGGER.error("Erro ao atualizar o Schedule no Health Check", e);
        }
    }

}
