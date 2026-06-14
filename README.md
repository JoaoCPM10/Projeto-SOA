---
# Sistema de Reservas - Condomínio

Plataforma de serviços para reserva de espaços em condomínio residencial, construída com arquitetura SOA (Service-Oriented Architecture) utilizando comunicação SOAP/WSDL.

---

## Como Rodar

### Pré-requisitos
- JDK 11 ou superior
- Maven instalado e adicionado ao PATH

### Passos
1. Clone o repositório
2. Abra a pasta do projeto no VSCode ou IntelliJ
3. Rode o `Servidor.java` para subir os endpoints SOAP
4. Rode o `Interface.java` para abrir a interface gráfica

O servidor precisa estar rodando antes de abrir a interface.

---

## Tecnologias

### Maven e pom.xml
O `pom.xml` é o arquivo de configuração central do projeto. Ele descreve quais bibliotecas externas o projeto precisa e como compilá-lo. O Maven lê esse arquivo e baixa automaticamente as dependências necessárias.

Dependências utilizadas:
- **H2**: banco de dados embutido
- **JAX-WS RI 2.3.5**: biblioteca para criar e consumir serviços SOAP

### JAX-WS
JAX-WS (Java API for XML Web Services) é a API do Java para criar e consumir serviços SOAP. Sem ele, seria necessário escrever manualmente o XML de cada mensagem, montar o envelope SOAP, criar um servidor HTTP e interpretar as requisições. O JAX-WS elimina tudo isso através de anotações:

- `@WebService`: marca a classe como um serviço SOAP
- `@WebMethod`: marca quais métodos ficam acessíveis via SOAP
- `@WebParam`: define o nome de cada parâmetro no XML

### Banco H2
Banco de dados relacional embutido que não requer instalação separada. O Maven baixa o H2 como dependência e ele sobe junto com o programa. Os dados ficam salvos no arquivo `condominio_db.mv.db` na pasta do projeto, persistindo entre reinicializações do servidor.

> O banco pode ser substituído por PostgreSQL ou MySQL alterando apenas a URL de conexão no `ConexaoBanco.java` e a dependência no `pom.xml`.

---

## Arquitetura

### Fluxo de comunicação
    1. Interface: janela Swing que o usuário opera. Quando o usuário preenche os campos e clica em um botão, a interface monta uma mensagem XML e a envia via HTTP para o servidor usando o protocolo SOAP.

    2. Endpoint: Porta de entrada do servidor. O Endpoint recebe o envelope SOAP, o JAX-WS extrai automaticamente os dados do XML e os converte para tipos Java, e o Endpoint chama o Service correspondente passando esses dados

    3. Service: Ele valida os dados recebidos (CPF, intervalo de datas, existência do espaço), toma decisões (o espaço está disponível? a reserva pertence a esse CPF?) e chama o Repository para buscar ou salvar dados no banco.

    4. Repository: Ele valida os dados recebidos (CPF, intervalo de datas, existência do espaço), toma decisões (o espaço está disponível? a reserva pertence a esse CPF?) e chama o Repository para buscar ou salvar dados no banco.

    5. Service: Recebe o resultado do Repository, monta um objeto de resposta (como ResultadoReserva ou ResultadoDisponibilidade) e o devolve ao Endpoint

    6. Recebe a resposta do Service, o JAX-WS a serializa automaticamente em XML dentro de um envelope SOAP e a envia de volta via HTTP para a Interface.

    7. Interface: Recebe o envelope SOAP, remove as tags XML e exibe o texto resultante na área de resultado da tela.

    ReservaScheduler: Roda em segundo plano desde que o servidor sobe. Todo dia à meia-noite ele dispara o ConcluirReservasCommand, que chama o Repository para buscar reservas confirmadas com data de fim anterior a hoje e atualiza o status delas para CONCLUIDA diretamente no banco, sem passar pela Interface nem pelos Endpoints.
    
### Camadas

**Models e Enums**
Classes que representam os dados do sistema. Não têm lógica de banco nem de rede.
- `DataReserva`: representa uma data com dia, mês e ano
- `IntervaloDatas`: representa um período entre duas datas, com verificação de sobreposição
- `Espaco`: representa um espaço do condomínio (churrasqueira, salão etc.)
- `EspacoFactory`: cria espaços com capacidades padrão por tipo
- `Reserva`: representa uma reserva feita por um morador
- `ResultadoDisponibilidade`: agrupa um espaço com seu status de disponibilidade
- `TipoEspaco`: enum com os tipos possíveis (CHURRASQUEIRA, SALAO_DE_FESTA, PISCINA, CAMPO_DE_FUTEBOL)
- `StatusReserva`: enum com os estados de uma reserva (CONFIRMADA, CONCLUIDA, CANCELADA)
- `StatusEspaco`: enum com os estados de disponibilidade (DISPONIVEL, INDISPONIVEL)

**Repositories**
Responsáveis por toda comunicação com o banco de dados. Os Services nunca falam diretamente com o banco, tudo passa pelos Repositories. Funcionam como Proxy do banco de dados.
- `EspacoRepository` / `EspacoRepositoryImpl`: busca espaços por id, tipo ou lista todos
- `ReservaRepository` / `ReservaRepositoryImpl`: salva, busca, atualiza e cancela reservas
- `ConexaoBanco`: abre a conexão com o H2, cria as tabelas e popula os dados iniciais

**Services**
Onde mora toda a lógica de negócio. Recebem dados, validam, tomam decisões e chamam os Repositories.
- `RealizarReservaService`: valida CPF, verifica disponibilidade e salva a reserva
- `ConsultaDisponibilidadeService`: retorna o status de todos os espaços em um período
- `ConsultarMinhasReservasService`: retorna as reservas de um morador em um período
- `RelatorioAdminService`: retorna todas as reservas de todos os moradores em um período
- `CancelarReservaService`: verifica se a reserva pertence ao CPF informado e cancela

**Endpoints**
Porta de entrada pela rede. Recebem a requisição SOAP, extraem os dados do XML, chamam o Service correspondente e devolvem a resposta em XML. Não têm lógica própria.
- `RealizarReservaEndpoint`: expõe o serviço de reserva em `http://localhost:8080/reserva`
- `ConsultaDisponibilidadeEndpoint`: expõe em `http://localhost:8080/disponibilidade`
- `ConsultarMinhasReservasEndpoint`: expõe em `http://localhost:8080/minhasReservas`
- `RelatorioAdminEndpoint`: expõe em `http://localhost:8080/relatorio`
- `CancelarReservaEndpoint`: expõe em `http://localhost:8080/cancelar`

**Command (padrão de projeto)**
Implementa o padrão Command para automatizar a conclusão de reservas passadas.
- `Command`: interface com o método `execute()`
- `ConcluirReservasCommand`: busca reservas confirmadas com data de fim anterior a hoje e muda o status para CONCLUIDA
- `ReservaScheduler`: agenda o comando para rodar automaticamente todo dia à meia-noite

---

## Espaços do Condomínio

Cadastrados automaticamente na primeira execução via `data.sql`:

| ID | Nome             | Tipo             | Capacidade |
|----|------------------|------------------|------------|
| 1  | Churrasqueira 1  | CHURRASQUEIRA    | 30 pessoas |
| 2  | Churrasqueira 2  | CHURRASQUEIRA    | 30 pessoas |
| 3  | Salão de Festas  | SALAO_DE_FESTA   | 100 pessoas|
| 4  | Piscina          | PISCINA          | 50 pessoas |
| 5  | Campo de Futebol | CAMPO_DE_FUTEBOL | 22 pessoas |

---

## Endpoints SOAP

Todos os WSDLs ficam disponíveis ao rodar o `Servidor.java`:

| Serviço                  | URL                                          |
|--------------------------|----------------------------------------------|
| Consultar Disponibilidade| http://localhost:8080/disponibilidade?wsdl   |
| Realizar Reserva         | http://localhost:8080/reserva?wsdl           |
| Minhas Reservas          | http://localhost:8080/minhasReservas?wsdl    |
| Relatório Admin          | http://localhost:8080/relatorio?wsdl         |
| Cancelar Reserva         | http://localhost:8080/cancelar?wsdl          |

---

## Interface Gráfica

A interface `Interface.java` simula dois clientes do sistema:

**App do Morador**
- Consultar Disponibilidade: verifica quais espaços estão livres em um período
- Realizar Reserva: reserva um espaço informando CPF, nome, espaço e período
- Minhas Reservas: lista as reservas do morador em um período
- Cancelar Reserva: cancela uma reserva ativa pelo CPF e ID da reserva

**Sistema da Portaria**
- Todas as funcionalidades do morador
- Relatório Admin: lista todas as reservas de todos os moradores em um período

Todas as chamadas da interface trafegam via SOAP, passando pelos Endpoints antes de chegar aos Services.

---

## Padrões de Projeto Utilizados

**Proxy**: os Repositories funcionam como Proxy do banco de dados. Os Services chamam métodos como `reservaRepo.findByEspaco(espaco)` sem saber que por trás há uma query SQL sendo executada.

**Factory**: a `EspacoFactory` encapsula a criação de espaços com capacidades padrão por tipo, evitando repetição de código.

**Command**: o `ConcluirReservasCommand` encapsula a ação de concluir reservas passadas. O `ReservaScheduler` dispara esse comando diariamente sem saber o que ele faz por dentro.


**Singleton**: o `ConexaoBanco` garante que todo o sistema use uma única conexão com o banco de dados.