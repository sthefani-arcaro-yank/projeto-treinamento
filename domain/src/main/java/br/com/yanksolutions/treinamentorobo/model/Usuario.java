package br.com.yanksolutions.treinamentorobo.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Usuario {

    @SerializedName("nick")
    private String nick;

    @SerializedName("cpf")
    private String cpf;
}
