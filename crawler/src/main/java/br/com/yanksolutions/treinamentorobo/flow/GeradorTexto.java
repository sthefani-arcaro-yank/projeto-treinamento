package br.com.yanksolutions.treinamentorobo.flow;

import br.com.yanksolutions.treinamentorobo.controller.pages.ImagemUtil;
import org.sikuli.script.*;
import org.springframework.stereotype.Component;

import java.io.File;


// projeto em sikuli/UI automation


@Component
public class GeradorTexto {

            // tela.find(new Pattern(new File("images/img.png").getAbsolutePath()).similar(0.8)).belowAt(25).left(15).click()

    public void geradorTextoInit() throws InterruptedException, FindFailed {
        String url = "https://www.4devs.com.br/gerador_de_texto_lorem_ipsum";

        Pattern imgQtdReferencia = ImagemUtil.carregarImagem("images/qtdReferencia.png");
        Pattern botaoGerarTexto = ImagemUtil.carregarImagem("images/gerarTexto.png");
        Pattern imgTextoReferencia = ImagemUtil.carregarImagem("images/textoGeradoRef.png");
        Pattern botaoArquivo = ImagemUtil.carregarImagem("images/arquivoNotepad.png");
        Pattern botaoSalvarArq = ImagemUtil.carregarImagem("images/salvarNotepad.png");

        Screen tela = new Screen();

        // abrir navegador e acessar site
        tela.type("r", Key.WIN);
        Thread.sleep(1000);
        tela.type("chrome --start-maximized " + url + Key.ENTER);

        // gerar texto
        tela.wait(imgQtdReferencia, 15).below(30).click();
        Thread.sleep(1000);

        tela.type("a", Key.CTRL);
        tela.type(Key.BACKSPACE);
        tela.type("2");
        Thread.sleep(3000);

        tela.wait(botaoGerarTexto, 15).getCenter().click();
        tela.wait(botaoGerarTexto, 15).getCenter().click();
        Thread.sleep(1000);


        tela.wait(imgTextoReferencia, 15).below(25).click();
        tela.type("a", Key.CTRL);
        tela.type("c", Key.CTRL);
        Thread.sleep(1000);

        // salvar texto
        tela.type("r", Key.WIN);
        Thread.sleep(1000);
        tela.type("notepad" + Key.ENTER);
        Thread.sleep(1000);
        tela.type(Key.UP, Key.WIN);
        Thread.sleep(1000);
        tela.type("v", Key.CTRL);
        Thread.sleep(1000);
        tela.type("s", Key.CTRL);
        Thread.sleep(1000);
        tela.type("treinamento" + Key.ENTER);
    }
}
