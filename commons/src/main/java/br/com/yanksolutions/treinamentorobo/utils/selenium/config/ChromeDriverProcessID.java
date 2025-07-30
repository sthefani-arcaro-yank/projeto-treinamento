package br.com.yanksolutions.treinamentorobo.utils.selenium.config;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class ChromeDriverProcessID {

    private static final Logger LOGGER = LogManager.getLogger("treinamento-robo");
    private static final String PATH = "/bin/sh";

    protected int getChromeDriverProcessID(int aPort) {
        try {
            String[] commandArray = new String[3];

            if (SystemUtils.IS_OS_LINUX) {
                commandArray[0] = PATH;
                commandArray[1] = "-c";
                commandArray[2] = "netstat -anp | grep LISTEN | grep " + aPort;
            } else if (SystemUtils.IS_OS_WINDOWS) {
                commandArray[0] = "cmd";
                commandArray[1] = "/c";
                commandArray[2] = "netstat -aon | findstr LISTENING | findstr " + aPort;
            } else {
                return 0;
            }

            Process p = Runtime.getRuntime().exec(commandArray);
            p.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            String result = sb.toString().trim();


            return SystemUtils.IS_OS_LINUX ? parseChromeDriverLinux(result) : parseChromeDriverWindows(result);
        } catch (Exception e) {
            LOGGER.error("Erro ao capturar os pid do chromedriver",e);
            return 0;
        }
    }

    protected int getChromeProcesID(int chromeDriverProcessID) throws IOException, InterruptedException {
        try {
            String[] commandArray = new String[3];

            if (SystemUtils.IS_OS_LINUX) {
                commandArray[0] = PATH;
                commandArray[1] = "-c";
                commandArray[2] = "ps -efj | grep google-chrome | grep " + chromeDriverProcessID;
            } else if (SystemUtils.IS_OS_WINDOWS) {
                commandArray[0] = "cmd";
                commandArray[1] = "/c";
                commandArray[2] = "wmic process get processid,parentprocessid,executablepath | find \"chrome.exe\" |find \"" + chromeDriverProcessID + "\"";
            } else
                return 0;

            Process p = Runtime.getRuntime().exec(commandArray);
            p.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (SystemUtils.IS_OS_LINUX && line.contains(PATH)) {
                    continue;
                }

                sb.append(line + "\n");
            }

            String result = sb.toString().trim();

            return SystemUtils.IS_OS_LINUX ? parseChromeLinux(result) : parseChromeWindows(result);
        } catch (Exception e) {
            LOGGER.error("Erro ao capturar os pid do chrome",e);
            return 0;
        }
    }

    private int parseChromeLinux(String result) {
        String[] pieces = result.split("\\s+");
        return Integer.parseInt(pieces[1]);
    }

    private int parseChromeWindows(String result) {
        String[] pieces = result.split("\\s+");
        return Integer.parseInt(pieces[pieces.length - 1]);
    }

    private int parseChromeDriverLinux(String netstatResult) {
        String[] pieces = netstatResult.split("\\s+");
        String last = pieces[pieces.length - 1];
        return Integer.parseInt(last.substring(0, last.indexOf('/')));
    }

    private int parseChromeDriverWindows(String netstatResult) {
        String[] pieces = netstatResult.split("\\s+");
        return Integer.parseInt(pieces[pieces.length - 1]);
    }

}
