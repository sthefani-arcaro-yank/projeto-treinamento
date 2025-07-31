package br.com.yanksolutions.treinamentorobo.controller.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

public class CapturaNicksPage {
    private WebDriver browser;
    private static final String URL = "https://www.4devs.com.br/gerador_de_texto_lorem_ipsum";

    public CapturaNicksPage(){
        System.setProperty("webdriver.chrome.driver", "drivers\\chromedriver.exe");
         this.browser = new ChromeDriver();
    }

    public void acessarPagina(){
        this.browser.navigate().to(URL);
    }

    public void fecharPagina(){
        this.browser.quit();
    }

    public void acessarGerador(String elemento){
        browser.findElement(By.xpath(elemento)).click();
        Assert.assertNotEquals(URL, browser.getCurrentUrl());
    }

    public void selecionarMetodo(String xpathSelect, String textoOpcao){
        WebElement elementoSelect = browser.findElement(By.xpath(xpathSelect));
        Select select = new Select(elementoSelect);
        select.selectByVisibleText(textoOpcao);
    }
}
