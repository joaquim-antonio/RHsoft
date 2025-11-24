[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/rdlwBGXm)
[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=20276750)
# RHSoft

![Capa do Projeto](https://picsum.photos/850/280)

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

O projeto **RHSoft** (SiriusDesktop) encontra-se atualmente em fase de **Protótipo de Alta Fidelidade (Frontend)**.

É fundamental notar que, para fins de demonstração da interface de usuário e da lógica de interação no lado do cliente, o sistema utiliza **dados mockados** (simulados) diretamente no código JavaScript.

**Atenção:**
> A implementação atual **não possui persistência de dados** (não salva informações em um banco de dados) e **não está integrada ao backend** (APIs em Java/Spring Boot). As ações de cadastro, edição e consulta são apenas simulações visuais no frontend.

---

## ⚙️ Funcionalidades Principais

- [x] **Fazer Login**
- [x] **Gestão de funcionários**
- [ ] Gestão da folha de pagamento
- [X] **Publicação e gerenciamento de vagas**
- [ ] Atualização de configurações
- [ ] Consulta de contracheques
- [ ] Atualização de dados cadastrais

## 🏗️ Arquitetura e Tecnologias

- **Back-end**: Java + Spring Boot (camadas controller, service, repository).
- **Front-end**: aplicação web em JavaScript puro.
- **Comunicação**: APIs REST + JSON.
- **Banco de dados**: relacional (Microsoft Azure).
- **Disponibilidade**: acesso via navegador moderno.

## 📂 Estrutura do Repositório

```
repo/
├─ backend/                  # API em Spring Boot
├─ frontend/                 # Aplicação web (JS puro ou framework)
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
│  └─ prototipos/            # protótipos do Figma
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

[Exemplo](https://github.com/testing-library/react-testing-library#contributors)

## 📜 Licença

Projeto acadêmico – uso restrito à disciplina. Este projeto está licenciado sob a Licença MIT, consulte o arquivo [LICENSE.md](LICENSE.md) para mais detalhes.

## Agradecimentos

Seção livre para você agradecer a todos que contribuiram para a execução do seu projeto.
