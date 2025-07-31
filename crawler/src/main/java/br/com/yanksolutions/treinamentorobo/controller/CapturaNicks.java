package br.com.yanksolutions.treinamentorobo.controller;

import br.com.yanksolutions.treinamentorobo.controller.pages.CapturaNicksPage;
import com.google.common.annotations.VisibleForTesting;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class CapturaNicks {
    private CapturaNicksPage capturaNicks;

    @Before
    public void beforeEach(){
        this.capturaNicks = new CapturaNicksPage();
        this.capturaNicks.acessarPagina();
    }

    @After
    public void afterEach() throws InterruptedException {
        Thread.sleep(10000);
        this.capturaNicks.fecharPagina();
    }

    @Test
    public void gerarNicks() throws InterruptedException {
        capturaNicks.acessarGerador("//*[@id=\"top-nav\"]/li[9]/a");
        Thread.sleep(5000);
        capturaNicks.selecionarMetodo("//*[@id=\"method\"]", "Aleatório");
        Thread.sleep(5000);
        capturaNicks.selecionarQuantidade("//*[@id=\"quantity\"]", 50);
        Thread.sleep(5000);
        capturaNicks.selecionarMetodo("//*[@id=\"limit\"]", "8");
        Thread.sleep(5000);
        capturaNicks.submiteBotao("//*[@id=\"bt_gerar_nick\"]");
    }
}
