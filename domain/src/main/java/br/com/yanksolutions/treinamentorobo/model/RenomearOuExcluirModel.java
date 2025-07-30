package br.com.yanksolutions.treinamentorobo.model;

import br.com.yanksolutions.treinamentorobo.enums.StatusExecEnum;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class RenomearOuExcluirModel {
    @Expose
    @SerializedName(value = "id")
    private long id;

    @SerializedName(value = "COMPANY", alternate = {"Company"})
    private String company;

    @SerializedName(value = "CONTACT", alternate = {"Contact"})
    private String contact;

    @SerializedName(value = "COUNTRY", alternate = {"Country"})
    private String country;

    @Expose
    @SerializedName(value = "TELEFONE")
    private String telefone;

    @Expose
    @SerializedName(value = "RG")
    private String rg;

    @Expose
    @Builder.Default
    @SerializedName(value = "status")
    private int status = 0;

    @Expose
    @SerializedName(value = "porcent_exec")
    @Setter(AccessLevel.NONE)
    private int porcentExec;

    @Expose
    @SerializedName(value = "cod_status")
    @Setter(AccessLevel.NONE)
    private int codStatus;

    @Expose
    @SerializedName(value = "status_execucao")
    @Setter(AccessLevel.NONE)
    private String statusExec;

    @Expose
    @Builder.Default
    @SerializedName(value = "instancia")
    private int instancia = 1;

    @Expose
    @SerializedName(value = "time_exec")
    private LocalDateTime timeExec;

    @Expose
    @SerializedName(value = "time_begin")
    private LocalDateTime timeBegin;


    @Builder.Default
    @SerializedName(value = "time_import")
    private LocalDateTime timeImport = LocalDateTime.now();


    public void setStatusExec(StatusExecEnum statusExecEnum) {
        this.statusExec = statusExecEnum.getStatusExec();
        this.codStatus = statusExecEnum.getCodStatus();
        this.porcentExec = statusExecEnum.getPercentExec();
    }


}
