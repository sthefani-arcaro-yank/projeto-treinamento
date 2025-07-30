package br.com.yanksolutions.treinamentorobo.healthcheck;

import lombok.Data;

@Data

public class HealthCheckStatus {

    private boolean status = true;

    public boolean getStatus() {
        return status;
    }
}
