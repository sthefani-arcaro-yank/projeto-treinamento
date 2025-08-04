package br.com.yanksolutions.treinamentorobo.controller.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class CapturaDadosPage {
    private final WebDriver browser;
    private static final String URL = "https://www.4devs.com.br/gerador_de_texto_lorem_ipsum";

    private WebElement esperar(String xpath) {
        return new WebDriverWait(browser, Duration.ofSeconds(30))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
    }

    public CapturaDadosPage(){
        this.browser = new ChromeDriver();
        this.browser.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(50));
    }

    public void acessarPagina(){
        this.browser.navigate().to(URL);
    }

    public void fecharPagina(){
        this.browser.quit();
    }
    private void scrollElemento(WebElement elemento) {
        JavascriptExecutor js = (JavascriptExecutor) browser;
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", elemento);
    }

    public void acessarGerador(String elemento){
        esperar(elemento).click();
        Assert.assertNotEquals(URL, browser.getCurrentUrl());
    }

    public void selecionarMetodo(String xpathSelect, String textoOpcao){
        WebElement elementoSelect = esperar(xpathSelect);
        Select select = new Select(elementoSelect);
        select.selectByVisibleText(textoOpcao);
    }

    public void selecionarQuantidade(String elemento, int quantidade){
        WebElement campo =  esperar(elemento);
        campo.clear();
        campo.sendKeys(String.valueOf(quantidade));
    }

    public void submiteBotao(String elemento){
        WebElement botao = esperar(elemento);
        scrollElemento(botao);
        botao.click();
    }

    public void selecionarPontuacao(boolean pontuacao) {
        String id = pontuacao ? "pontuacao_sim" : "pontuacao_nao";
        browser.findElement(By.id(id)).click();
    }

    public List<String> capturarDadosGerados(String elemento) {
        List<WebElement> dados = browser.findElements(By.xpath(elemento));
        return dados.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public String capturarCPFGerado(String elemento) {
        By localizador = By.xpath(elemento);
        new WebDriverWait(browser, Duration.ofSeconds(30))
                .until(ExpectedConditions.not(ExpectedConditions.textToBe(localizador, "Gerando...")));

      return browser.findElement(localizador).getText();
    }

}
