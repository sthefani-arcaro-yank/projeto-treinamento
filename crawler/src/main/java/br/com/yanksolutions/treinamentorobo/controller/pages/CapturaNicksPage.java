package br.com.yanksolutions.treinamentorobo.controller.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CapturaNicksPage {
    private WebDriver browser;
    private static final String URL = "https://www.4devs.com.br/gerador_de_texto_lorem_ipsum";

    private WebElement esperar(String xpath) {
        return new WebDriverWait(browser, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
    }


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

    public void selecionarQuantidade(String elemento, int quantidade){
        WebElement campo =  browser.findElement(By.xpath(elemento));
        campo.clear();
        campo.sendKeys(String.valueOf(quantidade));
    }

    public void submiteBotao(String elemento){
        browser.findElement(By.xpath(elemento)).click();
    }


}
