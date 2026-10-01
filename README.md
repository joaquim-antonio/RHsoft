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


## ⚙️ Funcionalidades Principais

- [x] **Fazer Login**
- [x] **Gestão de funcionários**
- [X] **Gestão da folha de pagamento**
- [x] Publicação e gerenciamento de vagas
- [X] **Atualização de configurações**
- [X] **Consulta de contracheques**
- [x] Atualização de dados cadastrais

## 🏗️ Arquitetura e Tecnologias

- **Back-end**: Java 21 + Spring Boot 3.5 (camadas controller, service, repository), JWT stateless.
- **Front-end**: aplicação web em JavaScript puro, servida por nginx.
- **Comunicação**: APIs REST + JSON (documentadas em OpenAPI/Swagger).
- **Banco de dados**: MySQL 8 no ambiente Docker; H2 em arquivo no desenvolvimento local.
- **Orquestração**: Docker Compose (MySQL + backend + frontend).
- **Documentação da API**: springdoc-openapi, integrated ao backend (Swagger UI).
- **Disponibilidade**: acesso via navegador moderno.

## 🚀 Como Executar

### Via Docker Compose (ambiente completo: banco + backend + frontend)

Pré-requisito: Docker com Compose v2.

```bash
cd codigo
cp .env.example .env      # ajuste as senhas e gere um JWT_SECRET:
                          #   openssl rand -base64 48
docker compose up --build
```

| Serviço | Endereço | Observação |
|---|---|---|
| Frontend | http://127.0.0.1:5500 | Porta fixa: o CORS do backend só aceita esta origem |
| Backend | http://localhost:8080 | API REST |
| MySQL | `localhost:3307` | Mapeado de 3306 para não conflitar com MySQL local |

Comandos úteis:

```bash
docker compose ps                    # estado e health dos containers
docker compose logs -f backend       # log do backend
docker compose down                  # derruba, preserva os dados no volume
docker compose down -v               # derruba e APAGA o banco (volume removido)
```

O Swagger UI fica **desligado** no perfil `docker` (`application-docker.properties`),
para não expor o discovery da API publicamente. Para consultá-lo, suba o backend localmente.

### Desenvolvimento local (sem Docker, banco H2 em arquivo consumido localmente.)

```bash
# Backend — H2 em ./data/testdb, Swagger em http://localhost:8080/swagger-ui/index.html
cd codigo/app && ./mvnw spring-boot:run

# Frontend — precisa ser servido em http://127.0.0.1:5500 (exigência de CORS do backend)
cd codigo/frontend/src && python3 -m http.server 5500 --bind 127.0.0.1
```

Execute **um por vez**: o H2 em arquivo trava com `Database may be already in use`
se o app estiver de pé na mesma pasta durante os testes Maven.

Testes: `cd codigo/app && ./mvnw test`

## 📂 Estrutura do Repositório

```
repo/
├─ codigo/                    # Todo o código executável vive aqui
│  ├─ docker-compose.yml      # Orquestra: MySQL + backend + frontend
│  ├─ .env.example            # Credenciais do Compose (copie para .env; não versionar)
│  │
│  ├─ app/                    # API em Spring Boot (Java 21 / Spring Boot 3.5)
│  │  ├─ Dockerfile           # Build multi-stage: Maven -> JRE Alpine, usuário não-root
│  │  ├─ .dockerignore
│  │  ├─ .mvn/
│  │  ├─ pom.xml
│  │  ├─ src/
│  │  │  ├─ main/
│  │  │  │  ├─ java/com/exemplo/app/
│  │  │  │  │  ├─ config/          # OpenApiConfig, DatabaseLoader (seed CBO 2002)
│  │  │  │  │  ├─ controller/     # Endpoints REST (anotados para o Swagger)
│  │  │  │  │  ├─ dto/             # Records de entrada/saída (documentados com @Schema)
│  │  │  │  │  ├─ exception/       # GlobalExceptionHandler e exceções de domínio
│  │  │  │  │  ├─ infra/security/  # SecurityConfig, SecurityFilter, TokenService
│  │  │  │  │  ├─ model/           # Entidades JPA
│  │  │  │  │  ├─ repository/
│  │  │  │  │  └─ service/         # Regras de negócio (folha, INSS/IRRF, candidaturas)
│  │  │  │  └─ resources/          # application.properties, application-docker.properties
│  │  │  └─ test/
│  │  └─ target/                # Artefatos de build (gerado, fora do versionamento)
│  │
│  └─ frontend/               # Aplicação web (JS puro, sem etapa de build)
│     ├─ Dockerfile           # nginx servindo src/ como estático
│     ├─ nginx.conf
│     ├─ .dockerignore
│     └─ src/
│        ├─ *.html            # Uma página por funcionalidade
│        └─ assets/
│           ├─ css/           # Estilização do front
│           │  ├─ global/
│           │  └─ pages/
│           ├─ images/        # Imagens
│           │  ├─ global/
│           │  └─ pages/
│           └─ js/
│              ├─ global/
│              ├─ pages/
│              └─ services/   # Cliente HTTP (axios/fetch) do backend
│
├─ docs/                      # Documentação geral do projeto
│  ├─ requisitos/             # Visão conceitual e de negócio
│  ├─ api/                    # Documentação da API
│  ├─ analise/                # Classes candidatas, CRC, diagrama do modelo conceitual
│  ├─ design/                 # Diagramas de classe, sequência, etc.
│  └─ wireframe/              # Protótipos do Figma
│
└─ README.md                  # Este arquivo (guia do repositório)

```
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
