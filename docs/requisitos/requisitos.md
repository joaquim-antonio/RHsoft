# Requisitos do Sistema – RHSoft

## 1. Introdução – Proposta do Sistema

O **RHSoft** é um sistema de Recursos Humanos cujo objetivo é apoiar a **gestão de pessoas** em uma organização.  
Ele centraliza dados de funcionários, automatiza o cálculo da folha de pagamento e organiza a publicação de vagas de emprego.  
Assim, busca-se reduzir erros, padronizar processos e facilitar o acesso às informações por administradores e funcionários.



## 1.1 Objetivos

- Informatizar processos de administração de funcionários.
- Automatizar o cálculo da folha mensal de pagamento (salário, descontos, benefícios e encargos).
- Organizar a publicação e gestão de vagas disponíveis.



## 1.2 Descrição do Problema

- O controle atual é manual, feito com planilhas e documentos em papel.
- Há erros recorrentes no cálculo da folha salarial.
- O processo de publicação e acompanhamento de vagas é descentralizado e pouco eficiente.

**Solução proposta**: um sistema informatizado que centralize dados, automatize cálculos e padronize o processo de vagas.



## 1.3 Funções do Produto

| Nº  | Função                       | Descrição                                                                 |
| --- | ---------------------------- | ------------------------------------------------------------------------- |
| 1   | Gestão de funcionários       | Inserir, alterar, excluir e consultar dados de funcionários.              |
| 2   | Gestão da folha de pagamento | Calcular salários líquidos considerando descontos, benefícios e encargos. |
| 3   | Publicação de vagas          | Criar, alterar, excluir e consultar vagas de emprego.                     |
| 4   | Atualizar configurações      | Definir data de fechamento da folha e regras de cálculo.                  |
| 5   | Consultar contracheque       | Funcionário acessa e baixa contracheques mensais.                         |
| 6   | Atualizar dados cadastrais   | Funcionário altera informações pessoais e bancárias.                      |



## 1.4 Usuários e Sistemas Externos

- **Administrador de RH**: cadastra funcionários, processa a folha e gerencia vagas.
- **Funcionário**: consulta contracheques e atualiza dados pessoais.
- **Candidato**: acessa e se inscreve nas vagas publicadas pela empresa.



## 1.5 Requisitos Não Funcionais e Tecnológicos

- **Arquitetura distribuída**: comunicação via APIs REST.
- **Back-end**: Java + Spring Boot, com separação em camadas (controller, service, repository).
- **Front-end**: aplicação web (JavaScript puro ou frameworks como React, Angular, Vue).
- **Integração**: front ↔ back via HTTP/HTTPS, usando JSON.
- **Persistência**: banco de dados relacional.
- **Disponibilidade**: acesso via navegador moderno.



## 2. Casos de Uso (visão geral)

1. **Gestão de Funcionários**
2. **Gestão da Folha de Pagamento**
3. **Publicação de Vagas**
4. **Consultar Contracheque**
5. **Atualizar Dados Cadastrais**
6. **Atualizar Configurações**

_Detalhes completos dos casos de uso estão descritos no documento PDF `req-rhsoft.pdf`._

---
