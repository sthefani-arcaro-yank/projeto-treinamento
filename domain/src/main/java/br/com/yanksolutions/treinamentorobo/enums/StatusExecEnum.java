package br.com.yanksolutions.treinamentorobo.enums;

import java.util.stream.Stream;

public enum StatusExecEnum {

    /**
     * Status temporários
     */
    INSERIDO(1, "Demanda inserida no banco de dados", 0),
    CAPTURANDO_TELEFONE(2, "Capturando telefone", 1),
    TELEFONE_CAPTURADO(3, "Telefone capturado", 30),
    CAPTURANDO_RG(4, "Capturando RG", 60),

    /**
     * Status finais
     */
    SUCESSO(5, "Processamento finalizado com sucess", 100),
    ERRO_TELEFONE(6, "Erro ao capturar o telefone", 100),
    ERRO_RG(7, "Erro ao capturar o RG", 100);

    StatusExecEnum(int codStatus, String statusExec, int percentExec) {
        this.codStatus = codStatus;
        this.statusExec = statusExec;
        this.percentExec = percentExec;
    }

    private final int codStatus;
    private final String statusExec;
    private final int percentExec;

    public int getCodStatus() {
        return codStatus;
    }

    public String getStatusExec() {
        return statusExec;
    }

    public int getPercentExec() {
        return percentExec;
    }

    public static StatusExecEnum findByStatusExec(String statusExec) {
        return Stream.of(StatusExecEnum.values())
                .filter(e -> e.getStatusExec().equalsIgnoreCase(statusExec))
                .findFirst()
                .orElse(null);
    }

    public static StatusExecEnum findByCodStatus(int codStatus) {
        return Stream.of(StatusExecEnum.values())
                .filter(e -> e.getCodStatus() == codStatus)
                .findFirst()
                .orElse(null);
    }
}