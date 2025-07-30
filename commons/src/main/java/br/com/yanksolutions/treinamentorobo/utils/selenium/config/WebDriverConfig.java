package br.com.yanksolutions.treinamentorobo.utils.selenium.config;

import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
@Builder
public class WebDriverConfig {
    @Builder.Default
    private boolean headless = false;
    @Builder.Default
    private boolean undetected = false;
    @Builder.Default
    private boolean localChromeProfile = false;
    @Builder.Default
    private boolean incognitoMode = false;
    @Builder.Default
    private boolean disableGpu = false;
    @Builder.Default
    private String pathDownload = "";
    @Builder.Default
    private boolean closeWithPids = false;
    @Builder.Default
    private boolean webDriverManager = true;
    @Builder.Default
    private List<String> pathExtensoes = Arrays.asList();
}
