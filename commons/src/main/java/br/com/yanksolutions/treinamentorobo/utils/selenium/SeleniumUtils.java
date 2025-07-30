package br.com.yanksolutions.treinamentorobo.utils.selenium;


import br.com.yanksolutions.treinamentorobo.utils.selenium.config.ChromeDriverProcessID;
import br.com.yanksolutions.treinamentorobo.utils.selenium.config.VnpSeverSelector;
import br.com.yanksolutions.treinamentorobo.utils.selenium.config.WebDriverConfig;
import br.com.yanksolutions.commons.enums.FileSortingStrategy;
import br.com.yanksolutions.commons.utils.ArquivosUtils;
import br.com.yanksolutions.commons.utils.WaitUtils;
import br.com.yanksolutions.treinamentorobo.constant.SeleniumConstant;
import br.com.yanksolutions.treinamentorobo.exception.SeleniumException;
import br.com.yanksolutions.treinamentorobo.utils.selenium.undetected.ChromeDriverBuilder;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ThreadGuard;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class SeleniumUtils extends ChromeDriverProcessID {

    private static final String ID_PROTON_VPN = "jplgfhpmjnbigmhklmmbgecoobifkmpa";
    private static final String CHROME_DATA_PROFILE_LINUX = "%s/.config/google-chrome/";
    private static final String CHROME_DATA_PROFILE_WINDOWS = "%s\\AppData\\Local\\Google\\Chrome\\User Data";
    private static final String PROFILE_NAME = "Default";
    private static final int DEFAULT_WAIT = 30;
    private ThreadLocal<WebDriver> driverLocal = new ThreadLocal<>();
    private ThreadLocal<List<Integer>> pidsLocal = new ThreadLocal<>();
    private final Logger LOGGER = LogManager.getLogger(this.getClass().getName());
    private final VnpSeverSelector vnpSeverSelector;


    public void initDriver(WebDriverConfig config) {
        if (config.isUndetected())
            initUndetectedChromeDriver(config);
        else
            initChormeDriver(config);
    }

    private void initChormeDriver(WebDriverConfig config) {
        int tentativas = 2;
        while (--tentativas >= 0) {
            ChromeDriverService chromeDriverService = null;
            try {
                WebDriver protectedDriver = null;
                ChromeOptions options = getChromeOptions(config);
                if (config.isCloseWithPids()) {
                    chromeDriverService = ChromeDriverService.createDefaultService();
                    int port = chromeDriverService.getUrl().getPort();
                    protectedDriver = ThreadGuard.protect(new ChromeDriver(chromeDriverService, options));
                    protectedDriver.manage().window().maximize();

                    int chromeDriverProcessID = getChromeDriverProcessID(port);
                    int chromeProcessID = getChromeProcesID(chromeDriverProcessID);


                    List<Integer> pids = new ArrayList<>();
                    pids.add(chromeProcessID);
                    pids.add(chromeDriverProcessID);
                    pidsLocal.set(pids);
                } else
                    protectedDriver = ThreadGuard.protect(new ChromeDriver(options));

                driverLocal.set(protectedDriver);

                if (!config.isHeadless())
                    driverLocal.get().manage().window().maximize();

                return;
            } catch (Exception e) {
                LOGGER.error(SeleniumConstant.ERRO_AO_INSTANCIA_WEBDRIVER, e);
                WaitUtils.waitSegundos(5);
                closeDriver();
                if (Objects.nonNull(chromeDriverService))
                    chromeDriverService.stop();
            }
        }
        throw new SeleniumException(SeleniumConstant.ERRO_AO_INSTANCIA_WEBDRIVER);
    }

    private void initUndetectedChromeDriver(WebDriverConfig config) {
        int tentativas = 2;
        while (--tentativas >= 0) {
            ChromeDriverService service = null;
            try {
                ChromeOptions options = getChromeOptions(config);
                String userHome = System.getProperty("user.home");
                Path cacheDirectory = Paths.get(userHome, ".cache");

                String path;

                if (config.isWebDriverManager()) {
                    String driverPath = cacheDirectory + "/selenium/chromedriver";
                    List<File> fileList = ArquivosUtils.getFileList(driverPath, true, FileSortingStrategy.LAST_MODIFIED_DESC);
                    path = fileList.get(0).getAbsolutePath();
                } else
                    path = WebDriverManager.chromedriver().getDownloadedDriverPath();

                service = new ChromeDriverService.Builder()
                        .usingDriverExecutable(new File(path))
                        .usingAnyFreePort()
                        .build();
                WebDriver chromeDriver = new ChromeDriverBuilder().build(options, path);
                driverLocal.set(chromeDriver);

                if (!config.isHeadless())
                    driverLocal.get().manage().window().maximize();
                return;
            } catch (Exception e) {
                LOGGER.error(SeleniumConstant.ERRO_AO_INSTANCIA_WEBDRIVER, e);
                WaitUtils.waitSegundos(5);
                closeDriver();
            } finally {
                service.close();
            }
        }
        throw new SeleniumException(SeleniumConstant.ERRO_AO_INSTANCIA_WEBDRIVER);
    }

    private ChromeOptions getChromeOptions(WebDriverConfig config) {
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();

        options.addArguments("window-size=1280,720");
        options.addArguments("--start-maximized");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--safebrowsing-disable-download-protection");
        options.addArguments("--disable-infobars");

        if (config.isIncognitoMode()) {
            options.addArguments("--incognito");
        }


        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--lang=en");


        if (config.isDisableGpu()) {
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-gpu");
        }

        if (config.isHeadless()) {
            options.addArguments("--headless=new");
        }

        if (StringUtils.isNotBlank(config.getPathDownload())) {
            File diretoryDownload = new File(config.getPathDownload());
            if (!diretoryDownload.exists()) {
                ArquivosUtils.createDirectory(diretoryDownload.getAbsolutePath());
            }
            prefs.put("download.default_directory", diretoryDownload.getAbsolutePath());
        }

        prefs.put("intl.accept_languages", "en");
        prefs.put("profile.default_content_settings.popups", 0);
        prefs.put("download.prompt_for_download", false);
        prefs.put("safebrowsing.enabled", "false");
        prefs.put("plugins.always_open_pdf_externally", true);
        prefs.put("safebrowsing.disable_download_protection", true);
        prefs.put("safebrowsing.disable_extension_blacklist", true);
        prefs.put("download.extensions_to_open", "application/pdf," +
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet," +
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document," +
                "application/msword," +
                "application/vnd.ms-excel," +
                "application/vnd.ms-powerpoint," +
                "text/plain," +
                "text/csv," +
                "image/jpeg," +
                "image/png," +
                "application/zip," +
                "application/x-rar-compressed," +
                "application/x-7z-compressed," +
                "application/x-tar," +
                "application/x-gzip");
        options.setExperimentalOption("prefs", prefs);

        String extensions = "";
        for (String path : config.getPathExtensoes()) {
            File extension = new File(path);
            if (StringUtils.isBlank(extensions)) {
                extensions = extension.getAbsolutePath();
            } else {
                extensions = extensions + "," + extension.getAbsolutePath();
            }
        }

        if (StringUtils.isNotBlank(extensions))
            options.addArguments("--load-extension=" + extensions);

        if (config.isLocalChromeProfile()) {
            if (SystemUtils.IS_OS_WINDOWS) {
                options.addArguments("--user-data-dir=" + CHROME_DATA_PROFILE_WINDOWS.formatted(System.getProperty("user.home")));
                options.addArguments("--profile-directory=" + PROFILE_NAME);
            } else {
                options.addArguments("--user-data-dir=" + CHROME_DATA_PROFILE_LINUX.formatted(System.getProperty("user.home")));
                options.addArguments("--profile-directory=" + PROFILE_NAME);
            }
        }

        return options;
    }

    public void connectToVnp(String server, String usuario, String senha) {
        String titulo = null;
        try {
            closeAllWindowsExceptCurrent();
            titulo = driverLocal.get().getTitle();
            openNewTab();
            openUrl("chrome-extension://" + ID_PROTON_VPN + "/popup.html");

            waitClickablesByXPath("//input[@type='search']", "//button[@class='sign-in-button']", 30);

            if (!isDisplayedByXpath("//input[@type='search']")) {
                clickByXpath("//button[@class=\"sign-in-button\"]");

                switchToWindowsByTitles(
                        List.of("Conta da Proton: Iniciar sessão", "Proton Account: Sign-in"),
                        30
                );

                waitClickableByXPath("//*[@id=\"username\"]", 30);
                setValueByXpath("//*[@id=\"username\"]", "");
                setValueByXpath("//*[@id=\"password\"]", "");


                WaitUtils.wait(250);
                waitMatchesTextByXpath("//*[@id=\"username\"]", "", 30);
                sendKeysByXpath("//*[@id=\"username\"]", usuario);
                sendKeysByXpath("//*[@id=\"password\"]", senha);
                WaitUtils.wait(250);

                String checked = getElementAttributeByXPath("//input[@id='staySignedIn']", "checked");

                if (StringUtils.isBlank(checked) || !checked.equals("true"))
                    driverLocal.get().findElement(By.xpath("//input[@id='staySignedIn']")).click();


                clickByXpath("//button[@type=\"submit\"]");
                WaitUtils.wait(250);
                waitPresenceByXPath("//h1[contains(text(),'signed in') or contains(text(),' iniciou sessão')]", 30);

                openUrl("chrome-extension://" + ID_PROTON_VPN + "/popup.html");
                WaitUtils.wait(250);
                waitClickableByXPath("//input[@type='search']", 30);

            }
            sendKeysByXpath("//input[@type='search']", server);

            waitPresenceByXPath("//button[div[text()='%s']]".formatted(server), 30);
            clickByXpath("//button[div[text()='%s']]".formatted(server));
            WaitUtils.wait(250);
            waitMatchesTextByXpath("//*[@id='protected-label']", "(Protected|Protegido)", 30);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_CONECTAR_VPN, e);
        } finally {
            if (Objects.nonNull(titulo)) {
                driverLocal.get().close();
                switchToWindowsByTitle(titulo, 10);
                closeAllWindowsExceptCurrent();
            }
        }
    }


    public void connectToVnp(String usuario, String senha) {
        String server = vnpSeverSelector.getRandomServer();
        connectToVnp(server, usuario, senha);
    }

    public void disconnectVnp() {
        try {
            String title = driverLocal.get().getTitle();
            openNewTab();
            WaitUtils.wait(250);
            openUrl("chrome-extension://" + ID_PROTON_VPN + "/popup.html");
            if (existsClickableByXpath("//button[contains(@class,'disconnection-button')]", 30)) {
                clickByXpath("//button[contains(@class,'disconnection-button')]");
                waitPresenceByXPath("//*[@id='unprotected-label']", 30);
            }

            driverLocal.get().close();
            switchToWindowsByTitle(title, 10);
            WaitUtils.wait(250);

        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_DESCONECTAR_VPN, e);
        }
    }


    public void closeDriver() {
        try {
            if (Objects.nonNull(driverLocal.get()))
                driverLocal.get().quit();
        } catch (Exception e) {
            LOGGER.error(SeleniumConstant.ERRO_FECHAR_WEBDRIVER, e);
        }
        driverLocal.set(null);

        if (Objects.nonNull(pidsLocal.get()) && pidsLocal.get().size() > 1) {
            closeByPid(0);
            closeByPid(1);
            pidsLocal.set(null);
        }
    }

    private void closeByPid(int index) {
        try {
            if (SystemUtils.IS_OS_WINDOWS)
                Runtime.getRuntime().exec(SeleniumConstant.TASKKILL + pidsLocal.get().get(index));
            else
                Runtime.getRuntime().exec(SeleniumConstant.KILLALL + pidsLocal.get().get(index));
        } catch (Exception e) {
            LOGGER.error(SeleniumConstant.ERRO_FECHAR_WEBDRIVER, e);
        }
    }


    public void closeTab() {
        try {
            driverLocal.get().close();
        } catch (Exception e) {
            LOGGER.error(SeleniumConstant.ERRO_FECHAR_ABA, e);
        }
    }

    public void refresh() {
        try {
            driverLocal.get().navigate().refresh();
        } catch (Exception e) {
            LOGGER.error(SeleniumConstant.ERRO_AO_ATUALIZAR_A_PAGINA, e);
        }
    }

    public void clearCookies() {
        try {
            driverLocal.get().manage().deleteAllCookies();
        } catch (Exception e) {
            LOGGER.error(SeleniumConstant.ERRO_AO_LIMPAR_COOKIES, e);
        }
    }

    public String getSource() {
        try {
            return driverLocal.get().getPageSource();
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_PEGAR_O_SOURCE, e);
        }
    }

    public void openUrl(String url, String xpathWait, int secondsWait) {
        try {
            driverLocal.get().get(url);
            waitPresenceByXPath(xpathWait, secondsWait);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ABRIR_A_URL, e);
        }

    }

    public void openUrl(String url) {
        try {
            driverLocal.get().get(url);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ABRIR_A_URL, e);
        }
    }

    public boolean compareUrl(String url) {
        try {
            return driverLocal.get().getCurrentUrl().equals(url);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_COMPARAR_URLS, e);

        }
    }

    public void switchToWindowsByTitle(String title, int time) {
        while ((time--) >= 0) {
            try {
                List<String> listWindows = new ArrayList<>(driverLocal.get().getWindowHandles());

                for (int index = listWindows.size() - 1; index >= 0; index--) {
                    driverLocal.get().switchTo().window(listWindows.get(index));
                    if (StringUtils.isNotBlank(driverLocal.get().getTitle()) && driverLocal.get().getTitle().equals(title))
                        return;
                }
            } catch (Exception e) {
                LOGGER.error(SeleniumConstant.ERRO_AO_TROCAR_DE_TAB, e);
            }
            WaitUtils.waitSegundos(1);
        }
        throw new SeleniumException(SeleniumConstant.ERRO_AO_TROCAR_DE_TAB);
    }


    public void switchToWindowsByTitles(List<String> titles, int time) {
        while ((time--) >= 0) {
            try {
                List<String> listWindows = new ArrayList<>(driverLocal.get().getWindowHandles());

                for (int index = listWindows.size() - 1; index >= 0; index--) {
                    driverLocal.get().switchTo().window(listWindows.get(index));
                    if (StringUtils.isNotBlank(driverLocal.get().getTitle()) && titles.stream().anyMatch(title -> title.equals(driverLocal.get().getTitle())))
                        return;
                }
            } catch (Exception e) {
                LOGGER.error(SeleniumConstant.ERRO_AO_TROCAR_DE_TAB, e);
            }
            WaitUtils.waitSegundos(1);
        }
        throw new SeleniumException(SeleniumConstant.ERRO_AO_TROCAR_DE_TAB);
    }

    public void switchToFrameByIndex(int index, int time) {
        while ((time--) >= 0) {
            try {
                driverLocal.get().switchTo().frame(index);
                return;
            } catch (Exception e) {
                LOGGER.error(SeleniumConstant.ERRO_AO_TROCAR_DE_FRAME, e);
            }
            WaitUtils.waitSegundos(1);
        }
        throw new SeleniumException(SeleniumConstant.ERRO_AO_TROCAR_DE_FRAME);
    }

    public void switchToFrameByName(String name, int time) {
        while ((time--) >= 0) {
            try {
                driverLocal.get().switchTo().frame(name);
                return;
            } catch (Exception e) {
                LOGGER.error(SeleniumConstant.ERRO_AO_TROCAR_DE_FRAME, e);
            }
            WaitUtils.waitSegundos(1);
        }
        throw new SeleniumException(SeleniumConstant.ERRO_AO_TROCAR_DE_FRAME);
    }

    public void switchToFrameByElement(WebElement element, int time) {
        while ((time--) >= 0) {
            try {
                driverLocal.get().switchTo().frame(element);
                return;
            } catch (Exception e) {
                LOGGER.error(SeleniumConstant.ERRO_AO_TROCAR_DE_FRAME, e);
            }
            WaitUtils.waitSegundos(1);
        }
        throw new SeleniumException(SeleniumConstant.ERRO_AO_TROCAR_DE_FRAME);
    }

    public void switchToFrameByXpath(String xpath, int time) {
        while ((time--) >= 0) {
            try {
                WebElement element = driverLocal.get().findElement(By.xpath(xpath));
                driverLocal.get().switchTo().frame(element);
                return;
            } catch (Exception e) {
                LOGGER.error(SeleniumConstant.ERRO_AO_TROCAR_DE_FRAME, e);
            }
            WaitUtils.waitSegundos(1);
        }
        throw new SeleniumException(SeleniumConstant.ERRO_AO_TROCAR_DE_FRAME);
    }

    public void switchToDefaultContent() {
        try {
            driverLocal.get().switchTo().defaultContent();
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_TROCAR_DE_FRAME, e);
        }
    }

    public void closeAllWindowsExceptCurrent() {
        try {
            List<String> listWindows = new ArrayList<>(driverLocal.get().getWindowHandles());
            listWindows.remove(driverLocal.get().getWindowHandle());


            for (int index = listWindows.size() - 1; index >= 0; index--) {
                driverLocal.get().switchTo().window(listWindows.get(index));
                ((JavascriptExecutor) driverLocal.get()).executeScript("window.close()");
                WaitUtils.waitSegundos(1);
            }
            return;
        } catch (Exception e) {
            LOGGER.error(SeleniumConstant.ERRO_FECHAR_AO_JANELAS, e);
        }
        throw new SeleniumException(SeleniumConstant.ERRO_FECHAR_AO_JANELAS);
    }


    public String acceptAlert(int waitSeconds) {
        String text = "";

        int tentativas = waitSeconds;
        while (--tentativas >= 0) {
            try {
                Alert alert = driverLocal.get().switchTo().alert();
                text = alert.getText();
                alert.accept();
                return text;
            } catch (Exception nape) {
                WaitUtils.waitSegundos(1);
            }
        }
        LOGGER.info(SeleniumConstant.NENHUM_ALERTA_ENCONTRADO);
        return text;
    }

    public String dismissAlert(int waitSeconds) {
        String text = "";

        int tentativas = waitSeconds;
        while (--tentativas >= 0) {
            try {
                Alert alert = driverLocal.get().switchTo().alert();
                text = alert.getText();
                alert.dismiss();
                return text;
            } catch (Exception nape) {
                WaitUtils.waitSegundos(1);
            }
        }
        LOGGER.info(SeleniumConstant.NENHUM_ALERTA_ENCONTRADO);
        return text;
    }

    public String getTextAlert(int waitSeconds) {
        String text = "";

        int tentativas = waitSeconds;
        while (--tentativas >= 0) {
            try {
                Alert alert = driverLocal.get().switchTo().alert();

                text = alert.getText();
                return text;
            } catch (Exception nape) {
                WaitUtils.waitSegundos(1);
            }
        }
        return text;
    }

    public boolean acceptAlertByText(int waitSeconds, String text) {
        String textAlert = "";

        int tentativas = waitSeconds;
        while (--tentativas >= 0) {
            try {
                Alert alert = driverLocal.get().switchTo().alert();

                textAlert = alert.getText();

                if (text.equals(textAlert)) {
                    alert.accept();
                    return true;
                } else {
                    alert.dismiss();
                    return false;
                }
            } catch (Exception nape) {
                WaitUtils.waitSegundos(1);
            }
        }

        throw new SeleniumException(SeleniumConstant.ERRO_AO_ACEITAR_OU_CANCELAR_ALERTA);
    }


    public Object executeScript(String script, WebElement webElement) {
        try {
            return ((JavascriptExecutor) driverLocal.get()).executeScript(script, webElement);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_EXECUTAR_JAVASCRIPT, e);
        }
    }

    public Object executeScript(String script) {
        try {
            return ((JavascriptExecutor) driverLocal.get()).executeScript(script);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_EXECUTAR_JAVASCRIPT, e);
        }
    }

    public void scrollToElement(By element) {
        try {
            WebElement webElement = driverLocal.get().findElement(element);
            ((JavascriptExecutor) driverLocal.get()).executeScript("arguments[0].scrollIntoView(true);", webElement);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_REALIZAR_O_SCROLL, e);
        }
    }

    public void scrollToElementByXpath(String xpath) {
        try {
            WebElement webElement = driverLocal.get().findElement(By.xpath(xpath));
            ((JavascriptExecutor) driverLocal.get()).executeScript("arguments[0].scrollIntoView(true);", webElement);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_REALIZAR_O_SCROLL, e);
        }
    }

    public void removeElement(By element) {
        try {
            WebElement webElement = driverLocal.get().findElement(element);
            ((JavascriptExecutor) driverLocal.get()).executeScript("arguments[0].remove();", webElement);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_REMOVER_ELEMENTO, e);
        }
    }

    public void removeElementByXpath(String xpath) {
        try {
            WebElement webElement = driverLocal.get().findElement(By.xpath(xpath));
            ((JavascriptExecutor) driverLocal.get()).executeScript("arguments[0].remove();", webElement);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_REMOVER_ELEMENTO, e);
        }
    }

    public void setInnerHtmlByXpath(String xpath, String html) {
        try {
            WebElement element = driverLocal.get().findElement(By.xpath(xpath));
            ((JavascriptExecutor) driverLocal.get()).executeScript("arguments[0].innerHTML = arguments[1];", element, html);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_REALIZAR_O_SET_INNER_HTML, e);
        }
    }

    public void setInnerHtml(By element, String html) {
        try {
            WebElement webElement = driverLocal.get().findElement(element);
            ((JavascriptExecutor) driverLocal.get()).executeScript("arguments[0].innerHTML = arguments[1];", webElement, html);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_REALIZAR_O_SET_INNER_HTML, e);
        }
    }


    public void setValue(By element, String html) {
        try {
            WebElement webElement = driverLocal.get().findElement(element);
            ((JavascriptExecutor) driverLocal.get()).executeScript("arguments[0].value = arguments[1];", webElement, html);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_REALIZAR_O_SET_INNER_HTML, e);
        }
    }

    public void setValueByXpath(String xpath, String html) {
        try {
            WebElement element = driverLocal.get().findElement(By.xpath(xpath));
            ((JavascriptExecutor) driverLocal.get()).executeScript("arguments[0].value = arguments[1];", element, html);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_REALIZAR_O_SET_INNER_HTML, e);
        }
    }

    public void changeAttributeValueByXpath(String xpath, String attribute, String value) {
        try {
            WebElement element = driverLocal.get().findElement(By.xpath(xpath));
            ((JavascriptExecutor) driverLocal.get()).executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", element, attribute, value);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_REALIZAR_O_SET_ATTRIBUTE, e);
        }
    }

    public void changeAttributeValueByElement(By element, String attribute, String value) {
        try {
            WebElement webElement = driverLocal.get().findElement(element);
            ((JavascriptExecutor) driverLocal.get()).executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", webElement, attribute, value);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_REALIZAR_O_SET_ATTRIBUTE, e);
        }
    }

    public void openNewTab() {
        try {
            driverLocal.get().switchTo().newWindow(WindowType.TAB);
        } catch (Exception e) {

            throw new SeleniumException(SeleniumConstant.ERRO_CRIAR_NOVA_ABA, e);
        }
    }


    public void selectValue(By element, String text) {
        try {
            waitPresenceByElement(element, DEFAULT_WAIT);
            Select cboActions = new Select(driverLocal.get().findElement(element));
            cboActions.selectByValue(text);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_SELECIONADOR_O_ELEMENTO, e);
        }
    }

    public void selectValueByXpath(String xpath, String text) {
        try {
            waitPresenceByXPath(xpath, DEFAULT_WAIT);
            Select cboActions = new Select(driverLocal.get().findElement(By.xpath(xpath)));
            cboActions.selectByValue(text);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_SELECIONADOR_O_ELEMENTO, e);
        }
    }

    public void selectVisibleText(By element, String text) {
        try {
            waitPresenceByElement(element, DEFAULT_WAIT);
            Select cboActions = new Select(driverLocal.get().findElement(element));
            cboActions.selectByVisibleText(text);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_SELECIONADOR_O_ELEMENTO, e);
        }
    }

    public void selectVisibleTextByXpath(String xpath, String text) {
        try {
            waitPresenceByXPath(xpath, DEFAULT_WAIT);
            Select cboActions = new Select(driverLocal.get().findElement(By.xpath(xpath)));
            cboActions.selectByVisibleText(text);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_SELECIONADOR_O_ELEMENTO, e);
        }
    }


    public boolean selectIfContainsText(By element, String text) {
        try {
            waitPresenceByElement(element, DEFAULT_WAIT);
            Select cboActions = new Select(driverLocal.get().findElement(element));

            Optional<WebElement> cbo = cboActions.getOptions().stream().filter(webElement -> webElement.getText().contains(text)).findFirst();

            if (cbo.isPresent()) {
                cbo.get().click();
                return true;
            } else
                return false;
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_SELECIONADOR_BY_CONTAINS_O_ELEMENTO, e);
        }


    }


    public boolean selectByXpathIfContainsText(String xpath, String text) {
        try {
            waitPresenceByXPath(xpath, DEFAULT_WAIT);
            Select cboActions = new Select(driverLocal.get().findElement(By.xpath(xpath)));

            Optional<WebElement> cbo = cboActions.getOptions().stream().filter(webElement -> webElement.getText().contains(text)).findFirst();

            if (cbo.isPresent()) {
                cbo.get().click();
                return true;
            } else
                return false;
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_SELECIONADOR_BY_CONTAINS_O_ELEMENTO);
        }
    }


    public WebElement getElement(By element) {
        waitPresenceByElement(element, DEFAULT_WAIT);
        return driverLocal.get().findElement(element);
    }

    public WebElement getElement(By element, int time) {
        waitPresenceByElement(element, DEFAULT_WAIT);
        return driverLocal.get().findElement(element);
    }


    public WebElement getElementByXpath(String xpath) {
        this.waitPresenceByXPath(xpath, DEFAULT_WAIT);
        return driverLocal.get().findElement(By.xpath(xpath));
    }

    public WebElement getElementByXpath(String xpath, int time) {
        this.waitPresenceByXPath(xpath, time);
        return driverLocal.get().findElement(By.xpath(xpath));
    }


    public String getElementAttributeByXPath(String xpath, String attribute) {
        try {
            this.waitPresenceByXPath(xpath, DEFAULT_WAIT);
            return driverLocal.get().findElement(By.xpath(xpath)).getAttribute(attribute);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_PEGAR_O_ABRIBUTO, e);
        }
    }

    public String getElementAttribute(By element, String attribute) {
        try {
            this.waitPresenceByElement(element, DEFAULT_WAIT);
            return driverLocal.get().findElement(element).getAttribute(attribute);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_PEGAR_O_TEXTO, e);
        }
    }

    public String getTextByXPath(String xpath) {
        try {
            this.waitPresenceByXPath(xpath, DEFAULT_WAIT);
            return driverLocal.get().findElement(By.xpath(xpath)).getText();
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_PEGAR_O_TEXTO, e);
        }
    }

    public String getText(By element) {
        try {
            this.waitPresenceByElement(element, DEFAULT_WAIT);
            return driverLocal.get().findElement(element).getText();
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_PEGAR_O_TEXTO);
        }
    }


    public void click(By element) {
        try {
            waitClickable(element, DEFAULT_WAIT);
            driverLocal.get().findElement(element).click();
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_REALIZAR_O_CLICK, e);
        }
    }

    public void clickByXpath(String xpath) {
        try {
            waitClickableByXPath(xpath, DEFAULT_WAIT);
            driverLocal.get().findElement(By.xpath(xpath)).click();
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_REALIZAR_O_CLICK, e);
        }
    }

    public void clickRightMouse(By element) {
        try {
            waitClickable(element, DEFAULT_WAIT);
            Actions action = new Actions(driverLocal.get());
            action.contextClick(driverLocal.get().findElement(element)).build().perform();
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_REALIZAR_O_CLICK, e);
        }
    }

    public void clickRightMouseByXpath(String xpath) {
        try {
            waitClickableByXPath(xpath, DEFAULT_WAIT);
            Actions action = new Actions(driverLocal.get());
            action.contextClick(driverLocal.get().findElement(By.xpath(xpath))).build().perform();
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_REALIZAR_O_CLICK, e);
        }
    }

    public void sendKeys(By element, String value) {
        try {
            waitPresenceByElement(element, DEFAULT_WAIT);
            driverLocal.get().findElement(element).clear();
            driverLocal.get().findElement(element).sendKeys(value);
        } catch (Exception e) {
            try {
                driverLocal.get().findElement(element).sendKeys(value);
            } catch (Exception ex) {
                throw new SeleniumException(SeleniumConstant.ERRO_AO_REALIZAR_O_SEND_KEYS, e);
            }
        }
    }

    public void sendKeysByXpath(String xpath, String value) {
        try {
            waitPresenceByXPath(xpath, DEFAULT_WAIT);
            driverLocal.get().findElement(By.xpath(xpath)).clear();
            driverLocal.get().findElement(By.xpath(xpath)).sendKeys(value);
        } catch (Exception e) {
            try {
                driverLocal.get().findElement(By.xpath(xpath)).sendKeys(value);
            } catch (Exception ex) {
                throw new SeleniumException(SeleniumConstant.ERRO_AO_REALIZAR_O_SEND_KEYS, e);
            }
        }
    }

    public JSONArray parseHtmlToTableToJsonWithHeader(By element) {
        try {
            waitPresenceByElement(element, DEFAULT_WAIT);
            WebElement table = driverLocal.get().findElement(element);
            List<WebElement> rows = table.findElements(By.tagName("tr"));
            JSONArray json = new JSONArray();
            for (int i = 0; i < rows.size(); i++) {
                JSONObject obj = new JSONObject();
                List<WebElement> columns = rows.get(i).findElements(By.tagName("td"));
                for (int j = 0; j < columns.size(); j++) {
                    obj.put("col" + j, columns.get(j).getText());
                }
                json.put(obj);
            }
            return json;
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_REALIZAR_O_PARSE, e);
        }
    }


    public JSONArray parseHtmlToTableToJson(By element) {
        try {
            waitPresenceByElement(element, DEFAULT_WAIT);
            WebElement table = driverLocal.get().findElement(element);
            List<WebElement> rows = table.findElements(By.tagName("tr"));
            JSONArray json = new JSONArray();

            Map<String, String> header = new HashMap();
            List<WebElement> columns = rows.get(0).findElements(By.tagName("td"));
            for (int j = 0; j < columns.size(); j++) {
                header.put("col" + j, columns.get(j).getText());
            }

            for (int i = 1; i < rows.size(); i++) {
                JSONObject obj = new JSONObject();
                columns = rows.get(i).findElements(By.tagName("td"));
                for (int j = 0; j < columns.size(); j++) {

                    if (header.containsKey("col" + j))
                        obj.put(header.get("col" + j), columns.get(j).getText());
                    else
                        obj.put("col" + j, columns.get(j).getText());
                }
                json.put(obj);
            }
            return json;
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_REALIZAR_O_PARSE, e);
        }
    }


    public JSONArray parseHtmlToTableToJsonWithHeaderByXpath(String xpath) {
        try {
            waitPresenceByXPath(xpath, DEFAULT_WAIT);
            WebElement table = driverLocal.get().findElement(By.xpath(xpath));
            List<WebElement> rows = table.findElements(By.tagName("tr"));
            JSONArray json = new JSONArray();
            for (int i = 0; i < rows.size(); i++) {
                JSONObject obj = new JSONObject();
                List<WebElement> columns = rows.get(i).findElements(By.tagName("td"));
                for (int j = 0; j < columns.size(); j++) {
                    obj.put("col" + j, columns.get(j).getText());
                }
                json.put(obj);
            }
            return json;
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_REALIZAR_O_PARSE, e);
        }
    }


    public JSONArray parseHtmlToTableToJsonByXpath(String xpath) {
        try {
            waitPresenceByXPath(xpath, DEFAULT_WAIT);
            WebElement table = driverLocal.get().findElement(By.xpath(xpath));
            List<WebElement> rows = table.findElements(By.tagName("tr"));
            JSONArray json = new JSONArray();

            Map<String, String> header = new HashMap();
            List<WebElement> columns = rows.get(0).findElements(By.tagName("th"));
            for (int j = 0; j < columns.size(); j++) {
                header.put("col" + j, columns.get(j).getText());
            }

            for (int i = 1; i < rows.size(); i++) {
                JSONObject obj = new JSONObject();
                columns = rows.get(i).findElements(By.tagName("td"));
                for (int j = 0; j < columns.size(); j++) {

                    if (header.containsKey("col" + j))
                        obj.put(header.get("col" + j), columns.get(j).getText());
                    else
                        obj.put("col" + j, columns.get(j).getText());
                }
                json.put(obj);
            }
            return json;
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_REALIZAR_O_PARSE, e);
        }
    }

    public List<WebElement> getElementsByXpath(String xpaths) {
        try {
            return driverLocal.get().findElements(By.xpath(xpaths));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_BUSCAR_ELEMENTOS, e);
        }
    }


    public List<WebElement> getElements(By elementos) {
        try {
            return driverLocal.get().findElements(elementos);
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_BUSCAR_ELEMENTOS, e);
        }
    }


    public boolean existsClickableByXpath(String xpath, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.or(
                    ExpectedConditions.elementToBeClickable(By.xpath(xpath))
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean existsClickable(By element, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.or(
                    ExpectedConditions.elementToBeClickable(element)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public boolean existsElementByXpath(String xpath) {
        try {
            List<WebElement> elements = driverLocal.get().findElements(By.xpath(xpath));

            return !elements.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean existsElement(By element) {
        try {
            List<WebElement> elements = driverLocal.get().findElements(element);

            return !elements.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDisplayedElement(By element) {
        try {
            return driverLocal.get().findElement(element).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDisplayedByXpath(String xpath) {
        try {
            return driverLocal.get().findElement(By.xpath(xpath)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean existsElement(By element, int segundos) {
        try {
            waitPresenceByElement(element, segundos);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean existsElementByXpath(String xpath, int segundos) {
        try {
            waitPresenceByXPath(xpath, segundos);
            return driverLocal.get().findElement(By.xpath(xpath)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDisplayedElement(By element, int segundos) {
        try {
            waitPresenceByElement(element, segundos);
            return driverLocal.get().findElement(element).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDisplayedByXpath(String xpath, int segundos) {
        try {
            waitPresenceByXPath(xpath, segundos);
            return driverLocal.get().findElement(By.xpath(xpath)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void waitVanishByXPath(String xpath, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_O_ELEMENTO, e);
        }
    }

    public void waitVanish(By element, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.invisibilityOfElementLocated(element));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_O_ELEMENTO, e);
        }
    }


    public void waitClickableByXPath(String xpath, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_O_ELEMENTO, e);
        }
    }

    public void waitClickable(By element, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_O_ELEMENTO, e);
        }
    }

    public void waitClickables(By element1, By element2, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.or(
                    ExpectedConditions.elementToBeClickable(element1),
                    ExpectedConditions.elementToBeClickable(element2)
            ));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_O_ELEMENTO, e);
        }
    }


    public void waitLoadTextByXpath(String xpath, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(d ->
                    StringUtils.isNotBlank(d.findElement(By.xpath(xpath)).getText())
            );
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_O_CARREGAMENTO, e);
        }
    }

    public void waitLoadText(By element, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(d ->
                    StringUtils.isNotBlank(d.findElement(element).getText())
            );
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_O_CARREGAMENTO, e);
        }
    }

    public void waitMatchesTextByXpath(String xpath, String regex, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.or(
                    ExpectedConditions.textMatches(By.xpath(xpath), Pattern.compile(regex))
            ));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_O_TEXTO_ELEMENTO, e);
        }
    }

    public void waitMatchesText(By element, String regex, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.or(
                    ExpectedConditions.textMatches(element, Pattern.compile(regex))
            ));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_O_TEXTO_ELEMENTO, e);
        }
    }

    public void waitMatchesTextOrClickableByXpath(String xpath, String xpathTextElement, String text, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.or(
                    ExpectedConditions.elementToBeClickable(By.xpath(xpath)),
                    ExpectedConditions.textMatches(By.xpath(xpathTextElement), Pattern.compile(text))
            ));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_O_TEXTO_ELEMENTO, e);
        }
    }

    public void waitMatchesTextOrClickable(By element, By elementText, String text, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.or(
                    ExpectedConditions.elementToBeClickable(element),
                    ExpectedConditions.textToBe(elementText, text)
            ));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_O_TEXTO_ELEMENTO, e);
        }
    }

    public void waitClickablesByXPath(String xpath1, String xpath2, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.or(
                    ExpectedConditions.elementToBeClickable(By.xpath(xpath1)),
                    ExpectedConditions.elementToBeClickable(By.xpath(xpath2))
            ));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_OS_ELEMENTOS, e);
        }
    }


    public void waitAttributeByXpath(String xpath, String attribute, String text, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.or(
                    ExpectedConditions.attributeContains(By.xpath(xpath), attribute, text)
            ));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_O_ATRIBUTO, e);
        }
    }

    public void waitAttribute(By element, String attribute, String text, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.or(
                    ExpectedConditions.attributeContains(element, attribute, text)
            ));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_O_ATRIBUTO, e);
        }
    }


    public void waitPresenceByXPath(String xpath, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_O_ELEMENTO, e);
        }
    }

    public void waitPresenceByElement(By element, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.presenceOfElementLocated(element));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_O_ELEMENTO, e);
        }
    }


    public void waitPresenceMatchesTextOrElementByXpath(String xpath, String xpathTextElement, String text, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)),
                    ExpectedConditions.textMatches(By.xpath(xpathTextElement), Pattern.compile(text))
            ));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_O_TEXTO_ELEMENTO, e);
        }
    }

    public void waitPresenceMatchesTextOrElement(By element, By elementText, String text, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(element),
                    ExpectedConditions.textToBe(elementText, text)
            ));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_O_TEXTO_ELEMENTO, e);
        }
    }

    public void waitPresenceByXPaths(String xpath1, String xpath2, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.xpath(xpath1)),
                    ExpectedConditions.presenceOfElementLocated(By.xpath(xpath2))
            ));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_OS_ELEMENTOS, e);
        }
    }

    public void waitPresenceByXPaths(String xpath1, String xpath2, String xpath3, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.xpath(xpath1)),
                    ExpectedConditions.presenceOfElementLocated(By.xpath(xpath2)),
                    ExpectedConditions.presenceOfElementLocated(By.xpath(xpath3))
            ));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_OS_ELEMENTOS, e);
        }
    }

    public void waitPresenceByXPaths(String xpath1, String xpath2, String xpath3, String xpath4, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.xpath(xpath1)),
                    ExpectedConditions.presenceOfElementLocated(By.xpath(xpath2)),
                    ExpectedConditions.presenceOfElementLocated(By.xpath(xpath3)),
                    ExpectedConditions.presenceOfElementLocated(By.xpath(xpath4))
            ));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_OS_ELEMENTOS, e);
        }
    }

    public void waitPresenceByElements(By by1, By by2, int segundos) {
        try {
            Duration durationInSecunds = Duration.ofSeconds(segundos);
            WebDriverWait isWait = new WebDriverWait(driverLocal.get(), durationInSecunds);
            isWait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(by1),
                    ExpectedConditions.presenceOfElementLocated(by1)
            ));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_ESPERAR_OS_ELEMENTOS, e);
        }
    }

    public boolean waitForDownloadCompletion(String downloadDirectory, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driverLocal.get(), Duration.ofSeconds(timeoutInSeconds));
        Path downloadDirPath = Paths.get(downloadDirectory);

        wait.until(driver -> {
            try {
                return Files.list(downloadDirPath).findFirst().isPresent();
            } catch (Exception e) {
                return false;
            }
        });

        return wait.until(driver -> {
            try {
                return Files.list(downloadDirPath)
                        .filter(path -> {
                            try {
                                long size1 = Files.size(path);
                                TimeUnit.SECONDS.sleep(1);
                                long size2 = Files.size(path);
                                return size1 == size2;
                            } catch (Exception e) {
                                return false;
                            }
                        })
                        .collect(Collectors.toList())
                        .size() > 0;
            } catch (Exception e) {
                return false;
            }
        });
    }

    public void takeScreenshotByElement(WebElement element, String path) {
        try {
            FileUtils.createParentDirectories(new File(path));
            File scrFile = element.getScreenshotAs(OutputType.FILE);
            BufferedImage bufferedImage = ImageIO.read(scrFile);
            String extension = path.substring(path.lastIndexOf(".") + 1);
            ImageIO.write(bufferedImage, extension, new File(path));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_TIRAR_SCREENSHOT, e);
        }
    }

    public void takeScreenshot(String path) {
        try {
            FileUtils.createParentDirectories(new File(path));
            File scrFile = ((TakesScreenshot) driverLocal.get()).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File(path));
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_TIRAR_SCREENSHOT, e);
        }
    }

    public String getPageSource() {
        try {
            return driverLocal.get().getPageSource();
        } catch (Exception e) {
            throw new SeleniumException(SeleniumConstant.ERRO_AO_PEGAR_O_CODIGO_HTML, e);
        }
    }

}
