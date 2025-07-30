# Treinamento Robo - projeto feito no treinamento de estágio em desenvolvimento

> Descrição do projeto: Projeto padrão para rpa
<br/>

## Informações Basicas

Número do Robô no Painel: 960 <br/>
Nome do Robô no Painel: - TESTE - ROBO <br/>
Cliente: Yank! Solutions <br/>
Desenvolvedor(a): Sthefani Arcaro  <br/>
Coordenador: Nome do coordenador  <br/>
Nome Squad: SQUAD_SUBSTITUIR  <br/>
PMO/ ANALISTA: Felipe Mello Freitas Silva   <br/>

    DATA CRIAÇÃO DO PROJETO: 30/07/2025
    DATA DE ENTRADA EM PRODUÇÃO: [Colocar a data de entrada em produção]

## Índice

* [1 - Repositório](#repositorio)
* [2 - Monitoramento](#monitoramento)
    * [2.1 - Horário, periodicidade e volumetria de execução](#horario)
    * [2.2 - Banco de dados/WS](#db)
    * [2.3 - Tabelas](#tabelas)
    * [2.4 - Requisitos de ambiente para execução](#requisitos)
    * [2.5 - Utiliza API ?](#api)
    * [2.6 - Log](#log)
    * [2.7 - Acesso a máquina: VPN (NOME DA VPN)](#acesso)
* [3 - Documentação Técnica (Códigos e fluxo)](#doc)
* [4 - Status](#status)

<div id='repositorio'/>  

## 1. Repositório:

Link: https://github.com/yanksolutions/treinamento-robo.git

__Branches:__<br/>

1. Branch de desenvolvimento: develop
2. Branch de produção: master

<div id='monitoramento'/>

## 2. Monitoramento

<div id='horario'/> 

### 2.1 Horário, periodicidade e volumetria de execução

Horário Início: 09:00  <br/>
Horário Fim: 18:00  <br/>
Periodicidade /
Dias de execução: segunda a sexta - a cada 1 hora   <br/>
Volumetria: 1000 casos por dia

<div id='db'/> 

### 2.2 Banco de dados/WS :

-[ ] Banco de Dados cliente
-[X] Bando de Dados Yank
-[X] WS Yank
-[ ] WS Externo – Cliente

<div id='tabelas'/>  

### 2.3 Tabelas:

Atualmente o robô trabalha com as tabela:

* ROBO_TABELA_SUBSTITUIR: é a tabela principal do projeto, onde serão salvos todos os casos processados.
* yank_nomeTabela: é a tabela auxiliar onde estão os modelos para validação.
* demanda: é a tabela no banco de dados cliente onde estão os casos que serão processados.

<div id='requisitos'/>  

### 2.4 Requisitos de ambiente para execução:

#### __Ambiente__

-[ ] Yank!
-[X] Cliente

#### __Versão Java__

- JDK (Java Development Kit): 24

#### __Tecnologias utilizadas__

- [X] Selenium (Driver: chormedriver)
    - Caminho do driver: webdrivermanager
- [ ] HtmlUnit
- [ ] Sikuli
- [ ] Jacob
- [ ] Robot
- [ ] UiAutomation

<div id='api'/>

#### 2.5 Utiliza API ?

- [x] Sim
- [ ] Não

__Se sim, qual e caminho da API?__<br/>
URL Base: https://urlbase.com/

__Endpoint utilizados:__<br/>

1. Endpoint pra pegar os dados do cliente

```bash
curl -X GET "https://urlbase.com/" -H "accept: application/json"
```

<div id='log'/>

### 2.6 Log:

* Caminho do log: \log\treinamento-robo.log

<div id='acesso'/>

### 2.7 Acesso a máquina: VPN (NOME DA VPN)

* Maquina: IP DA MÁQUINA

* Usuário: USUÁRIO DA MAQUINA

* Senha: SENHA DA MAQUINA

* Caminho do projeto: C:\CAMINHO DA MAQUINA NA MAQUINA ONDE ESTÁ ALOCADA

* __Obs.: quando utilizar FortClient, adicionar o caminho das configurações que deverão estar no OneDrive da pasta do
  cliente da Squad.__

<div id='doc'/>

## 3. Documentação Técnica (Códigos e fluxo)

Descrição de regras de negócio e fluxo do robô

O robô realiza alguns filtros no Soft para captura de input dos casos, em seguida, ele verifica se os modelos estão
inclusos na tabela auxiliar do projeto e por ultimo realiza o lançamento do agendamento.

##### 3.1 Captura da demanda

Para realizar a captura de demanda, é necessário seguir os filtros descritos na E.F.

__Trecho de código da classe X que realiza o filtro__

```java
     List<DemandaModel> demandaLiberadaList = demandaList.stream()
        .filter(demanda -> demanda.getTipoOcorrencia().equals("MANUTENÇÃO PREVENTIVA"))
        .filter(demanda -> demanda.getTipoVeiculo().equals("CAMINHÃO"))
        .filter(demanda -> demanda.getTipoServico().equals("MANUTENÇÃO PREVENTIVA"))
        .filter(demanda -> demanda.getTipoServico().equals("MANUTENÇÃO PREVENTIVA"))
        .filter(demanda -> demanda.getTipoServico().equals("MANUTENÇÃO PREVENTIVA"))
        .collect(Collectors.toList());
```

##### 3.2. Agendamento

Para seguir para o agendamento, o veículo deve possuir alguns critérios -> ter mais de 10 meses de idade, a KM atual
estar no mínimo 9.000 acima da KM da última troca do filtro
de oléo ou a data da última OS, deve ser no mínimo 11 meses abaixo da data atual.

##### 3.3. Seleção do fornecedor

E por ultimo deve comparar as distâncias dos endereços (se necessário) e selecionar um fornecedor sempre com Acordo
comercial com a LM.

<div id='status'/>

## 4. Status de execução

Tabelas com os status de execução do robô

| Status                        	 | Tipo           	 | Descrição                                                                                                                                                                                                                                                    	 |
|---------------------------------|------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Finalizado com sucesso        	 | Sucesso        	 | Veiculo cadastrado com sucesso no portal                                                                                                                                                                                                                     	 |
| Demanda capturada com sucesso 	 | Temporario     	 | Demanda capturada com sucesso                                                                                                                                                                                                                                	 |
| Veículo não consta na base    	 | Não processado 	 | Veículo não atende algum requisito para o agendamento, pode ser, idade inferior a 10 meses, a KM atual não estar no mínimo 9.000 acima da KM da última troca do filtro de oléo ou a data da última OS, deve não ser no mínimo 11 meses abaixo da data atual. 	 |
| Erro ao realizar agendamento  	 | Erro           	 | Houve um erro ao realizar o agendamento.                                                                                                                                                                                                                     	 |

Gerador de tabela pra readme.md: [Tables Generator](https://www.tablesgenerator.com/markdown_tables#)