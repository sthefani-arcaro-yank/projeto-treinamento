# Deplou no Docker

### 1. Mudar o caminho do log

Abrir o arquivo log4j2.xml e alterar o caminho do log pra /app/treinamento-robo/logs

### 2. Chrome deve estar no modo headless ou utilize o xvfb pra emular a tela

```bash


### 3 - Gerar o jar

```bash
mvn clean compile install
```

## 4. Criar volume

criar volume /jar na mesma pasta do docker-compose.yml e colocar o jar do projeto

### Obs.: caso precise tirar print ou usar uma versão específica do chrome, é necessário alterar o Dockerfile

### 5. Criar imagem e container

```bash
docker compose up -d
```