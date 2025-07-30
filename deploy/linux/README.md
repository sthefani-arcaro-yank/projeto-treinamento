# Deplou no Linux

### 1. Mudar o caminho do log

Abrir o arquivo log4j2.xml e alterar o caminho do log pra /opt/yank/treinamento-robo/logs

Obs: rodar o chrome no modo headless e caso o rpa manipule arquivos é necessario mudar os caminhos

### 2 - Gerar o jar

```bash
    mvn clean compile install
```

## 3. Executar o treinamento-robo-setup.sh

Ao executar os comandos, será criado a pasta rpa-teste-01 em /opt/yank/ e ele
também será criado o stript pra iniciar e pausar o rpa.
Além disso será configurado o crontab para executar junto com o sistema.

```bash
sudo su
# Cria a pasta /opt/yank/treinamento-robo
sudo mkdir -p /opt/yank/treinamento-robo

# Define permissões 777 para a pasta
sudo chmod 777 /opt/yank/treinamento-robo

# Cria o arquivo de inicialização é possível utilizar o arquivo gerado na pasta target do crawler
sudo tee /opt/yank/treinamento-robo/treinamento-robo-startup.sh << 'EOF'
#!/bin/bash
#caso precise tirar print
#xvfb-run -a --server-args="-screen 0 1024x720x24" java -jar /opt/yank/treinamento-robo/treinamento-robo.jar &
java -jar /opt/yank/treinamento-robo/treinamento-robo.jar &
EOF

# Cria o arquivo de pausa é possível utilizar o arquivo gerado na pasta target do crawler
sudo tee /opt/yank/treinamento-robo/treinamento-robo-shutdown.sh << 'EOF'
#!/bin/bash
pids=$(ps aux | grep 'treinamento-robo' | awk '{print $2}')
for pid in $pids; do
    kill -9 $pid
done
EOF

# Torna o arquivo treinamento-robo-start.sh executável
sudo chmod +x /opt/yank/treinamento-robo/treinamento-robo-start.sh

# Adiciona a execução do script no crontab ao reiniciar o sistema
(crontab -l 2>/dev/null; echo "@reboot sh /opt/yank/treinamento-robo/treinamento-robo-start.sh") | crontab
echo "Configuração finalizada"
```

## Opicional

### Instalar java 24

```bash 
sudo add-apt-repository ppa:openjdk-r/ppa
apt-get update
sudo apt-get install openjdk-24-jdk
```

### Instalar chrome

```bash
apt-get update && apt-get install -y wget unzip curl dpkg gnupg
wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list' && \
    apt-get update &&  \
    apt-get install -y google-chrome-stable &&  \
    rm -rf /var/lib/apt/lists/*
```

### Instalar xvfb

Utilize o xvfb para emular a tela

```bash
sudo apt-get install -y xvfb
```

## 4. Colocar o jar na pasta treinamento-robo

Usar o winscp para copiar o arquivo treinamento-robo.jar para a pasta /opt/yank/treinamento-robo

## 5. Execução

Para executar o rpa, basta executar o comando abaixo:

```bash
cd /opt/yank/treinamento-robo/
sh rtreinamento-robo-start.sh 
```

