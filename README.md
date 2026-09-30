# RHSoft

O RHSoft é um sistema de Recursos Humanos desenvolvido com o objetivo de apoiar a gestão de pessoas em organizações, oferecendo uma solução integrada para o cadastro e administração de funcionários, processamento da folha de pagamento e gerenciamento de vagas de emprego. O sistema centraliza informações essenciais, reduz erros operacionais e promove maior eficiência e confiabilidade nos processos de RH, disponibilizando recursos tanto para administradores quanto para funcionários de forma simples e segura.

## Proposta do Sistema

O RHSoft tem como objetivo **informatizar a gestão de recursos humanos** em uma organização, centralizando dados de funcionários, automatizando cálculos da folha de pagamento e organizando a publicação de vagas de emprego.

Problemas que o sistema resolve:

- Redução de erros no cálculo manual da folha.
- Armazenamento seguro e centralizado das informações.
- Organização estruturada da publicação e gestão de vagas.

### Objetivos

- Apoiar a administração de funcionários.
- Automatizar a folha mensal de pagamento (salário, benefícios, encargos).
- Facilitar a divulgação e gestão de vagas.
- Prover acesso seguro a dados para administradores e funcionários.

### Usuários do Sistema

- **Administrador de RH**: gerencia funcionários, folha e vagas.
- **Funcionário**: consulta contracheques, atualiza dados.
- **Candidato**: acessa e se inscreve nas vagas publicadas.

## ⚠️ Status Atual do Projeto

O **RHSoft (Sirius)** atingiu a versão **1.0 (Final)**.

O sistema opera de ponta a ponta:
- [x] **Frontend:** Interface completa.
- [x] **Backend:** Regras de negócio implementadas.
- [x] **Banco de Dados:** Conexão ativa e persistência de dados real.

---

## ⚙️ Funcionalidades Principais

- [x] **Fazer Login**
- [x] **Gestão de funcionários**
- [X] **Gestão da folha de pagamento**
- [x] Publicação e gerenciamento de vagas
- [X] **Atualização de configurações**
- [X] **Consulta de contracheques**
- [x] Atualização de dados cadastrais

## 🏗️ Arquitetura e Tecnologias

- **Back-end**: Java + Spring Boot (camadas controller, service, repository).
- **Front-end**: aplicação web em JavaScript puro.
- **Comunicação**: APIs REST + JSON.
- **Banco de dados**: relacional (Microsoft Azure).
- **Disponibilidade**: acesso via navegador moderno.

## 📂 Estrutura do Repositório

```
repo/
├─ app/                      # API em Spring Boot
│  ├─ .mvn/
│  ├─ src/
│  │  ├─ main/
│  │  │  ├─ java/com/exemplo/app/
│  │  │  │  ├─ config/
│  │  │  │  ├─ controller/
│  │  │  │  ├─ dto/
│  │  │  │  ├─ exception/
│  │  │  │  ├─ infra/security/
│  │  │  │  ├─ model/
│  │  │  │  ├─ repository/
│  │  │  │  └─ service/
│  │  │  └─ resources/
│  │  └─ test/
│  └─ target/            
├─ frontend/                 # Aplicação web (JS Puro)
│  └─ src/
│     ├─ assets/          
│     │  ├─ css/             # Estilização do Front
│     │  │  ├─ global/ 
│     │  │  └─ pages/
│     │  ├─ images/          # Imagens
│     │  │  ├─ global/ 
│     │  │  └─ pages/
│     │  └─ js/
│     │     ├─ global/
│     │     ├─ pages/
│     │     └─ services/   
│     └─ components/
├─ docs/                     # Documentação geral do projeto
│  ├─ requisitos/            # visão conceitual e de negócio
│  │  ├─ requisitos.md
│  │  └─ requisitos-rhsoft.pdf
│  │
│  ├─ api/                   # documentação da API
│  │  ├─ api.md              # visão geral da API
│  │  ├─ openapi.yaml        # especificação formal (se usarem Swagger/OpenAPI)
│  │  └─ exemplos/           # exemplos de requests/responses
│  │     ├─ cadastro-funcionario.json
│  │     ├─ folha-pagamento.json
│  │     └─ ...
│  │
│  ├─ analise/               # classes candidatas, CRC, diagrama do modelo conceitual
│  ├─ design/                # diagramas de classe, sequência, etc.
│  └─ wireframe/             # protótipos do Figma
│
└─ README.md                 # Este arquivo (guia do repositório)

```

## 📑 Documentação

- [Requisitos do Sistema](docs/requisitos.md)
- [Descrição da API](docs/api.md)
- [Protótipos](docs/arquitetura.md)

## Contribuição

Leia o arquivo [CONTRIBUTING.md](CONTRIBUTING.md) para saber detalhes sobre o nosso código de conduta e o processo de envio de solicitações _pull_ (_Pull Request_) para nós.
Todos os commits devem seguir o padrão [Conventional Commits](https://www.conventionalcommits.org/).

## Autores

**[Joaquim Camargos](https://github.com/joaquim-antonio)**

**[Pedro Matos](https://github.com/N16Kss)**

**[Pedro Rezende](https://github.com/pedrolsrt)**

**[Wesley Domingos](https://github.com/WesleySDz)**

## 📜 Licença

Projeto acadêmico – uso restrito à disciplina. Este projeto está licenciado sob a Licença MIT, consulte o arquivo [LICENSE.md](LICENSE.md) para mais detalhes.

## Agradecimentos

Seção livre para você agradecer a todos que contribuiram para a execução do seu projeto.
