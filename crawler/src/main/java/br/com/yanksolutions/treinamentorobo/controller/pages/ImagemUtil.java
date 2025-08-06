package br.com.yanksolutions.treinamentorobo.controller.pages;

import org.sikuli.script.Pattern;

import java.io.File;

public class ImagemUtil {

    public static Pattern carregarImagem(String caminhoRelativo) {
        File imagem = new File(caminhoRelativo);
        return new Pattern(imagem.getAbsolutePath());
    }
}
