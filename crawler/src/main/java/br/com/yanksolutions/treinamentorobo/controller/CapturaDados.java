package br.com.yanksolutions.treinamentorobo.controller;

import br.com.yanksolutions.treinamentorobo.controller.pages.CapturaDadosPage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CapturaDados {
    private CapturaDadosPage capturaDados;

    @Before
    public void beforeEach(){
        this.capturaDados = new CapturaDadosPage();
        this.capturaDados.acessarPagina();
    }

    @After
    public void afterEach() throws InterruptedException {
        Thread.sleep(10000);
        this.capturaDados.fecharPagina();
    }

    @Test
    public void gerarNicks() throws InterruptedException {
        capturaDados.acessarGerador("//a[@href='/gerador_de_nicks']");
        capturaDados.selecionarMetodo("//select[@id='method']", "Aleatório");
        capturaDados.selecionarQuantidade("//input[@id='quantity']", 50);
        capturaDados.selecionarMetodo("//select[@id='limit']", "8");
        capturaDados.submiteBotao("//input[@id='bt_gerar_nick']");
    }

    @Test
    public void gerarCPF() throws InterruptedException {
        capturaDados.acessarGerador("//a[@title='Gerador de CPF']");
        capturaDados.selecionarPontuacao(false);
        capturaDados.submiteBotao("//input[@id='bt_gerar_cpf']");
    }

}
