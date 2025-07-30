package br.com.yanksolutions.treinamentorobo.utils.selenium.config;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class VnpSeverSelector {
    private final static List<String> SERVER_LIST = List.of(
            "Los Angeles",
            "Osaka",
            "Buenos Aires",
            "Santiago",
            "Chicago",
            "Miami",
            "Berlim",
            "Sydney",
            "Quito",
            "Seul"
    );
    private List<String> servers = List.of("Los Angeles", "Osaka");
    private List<String> usedServers = new ArrayList<>();
    private Random random = new Random();

    public VnpSeverSelector() {
        this.servers = new ArrayList<>(SERVER_LIST);
    }

    public String getRandomServer() {
        if (servers.isEmpty()) {
            // All proxies have been used, repopulate the list
            this.servers = new ArrayList<>(SERVER_LIST);
            usedServers.clear();
        }

        int index = random.nextInt(servers.size());
        String selectedProxy = servers.get(index);

        // Move the used proxy from the list of available proxies to the list of used proxies
        servers.remove(index);
        usedServers.add(selectedProxy);

        return selectedProxy;
    }

}