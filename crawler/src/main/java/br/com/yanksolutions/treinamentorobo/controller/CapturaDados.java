package br.com.yanksolutions.treinamentorobo.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import br.com.yanksolutions.treinamentorobo.controller.model.Usuario;
import br.com.yanksolutions.treinamentorobo.controller.pages.CapturaDadosPage;
import br.com.yanksolutions.treinamentorobo.wsyank.WebServiceRequests;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class CapturaDados implements CommandLineRunner {

    @Autowired
    private CapturaDadosPage capturaDados;

    @Autowired
    private WebServiceRequests webServiceRequests;

    @Value("${robo.tabela}")
    private String tabela;

    @Override
    public void run(String... args) {
        System.out.println("entra em captura dados");
        try {

           System.out.println("tabela: " + tabela);
            capturaDados.acessarPagina();

            // coletar nicks
            capturaDados.acessarGerador("//a[@href='/gerador_de_nicks']");
            capturaDados.selecionarMetodo("//select[@id='method']", "Aleatório");
            capturaDados.selecionarQuantidade("//input[@id='quantity']", 50);
            capturaDados.selecionarMetodo("//select[@id='limit']", "8");
            capturaDados.submiteBotao("//input[@id='bt_gerar_nick']");

            List<String> nicks = capturaDados.capturarDadosGerados("//div[@id='nicks']//span[contains(@class, 'generated-nick')]");
            System.out.println("Nicks coletados: " + nicks);

            // coletar cpfs
            capturaDados.acessarPagina();
            capturaDados.acessarGerador("//a[@title='Gerador de CPF']");

            List<String> cpfs = new ArrayList<>();
            for (int i = 0; i < nicks.size(); i++) {
                capturaDados.selecionarPontuacao(false);
                capturaDados.submiteBotao("//input[@id='bt_gerar_cpf']");
                String cpf = capturaDados.capturarCPFGerado("//div[@id='texto_cpf']");
                cpfs.add(cpf);
            }
            System.out.println("CPFs coletados: " + cpfs);

            // integrar nicks e cpfs
            if (nicks.size() != cpfs.size()) {
                System.out.println("número de nicks e cpfs coletados são divergentes.");
                return;
            }

            for (int i = 0; i < nicks.size(); i++) {
                String nickUsuario = nicks.get(i);
                String cpfUsuario = cpfs.get(i);

                Usuario novoUsuario = new Usuario(nickUsuario, cpfUsuario);

                webServiceRequests.post(novoUsuario, tabela);
            }
        } finally {
            if (capturaDados != null) {
                capturaDados.fecharPagina();
            }
        }
    }
}
