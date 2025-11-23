# Documentação da API – RHSoft

Esta seção descreve os principais **endpoints REST** da aplicação RHSoft.  
Os exemplos utilizam **JSON** como formato de entrada e saída.

---

## 🔑 Autenticação
- **Método**: Bearer Token (JWT)  
- **Header**:  
  ```
  Authorization: Bearer <token>
  ```

---

## 👥 Funcionários

### POST /api/funcionarios
Cadastrar novo funcionário.
- **Request body**
```json
{
  "nome": "Maria Silva",
  "cpf": "12345678900",
  "cargo": "Analista",
  "salario": 5000.0
}
```
- **Response**
```json
{
  "id": 1,
  "mensagem": "Funcionário cadastrado com sucesso"
}
```

### GET /api/funcionarios/{id}
Consultar dados de um funcionário por ID.
- **Response**
```json
{
  "id": 1,
  "nome": "Maria Silva",
  "cpf": "12345678900",
  "cargo": "Analista",
  "salario": 5000.0
}
```

---

## 💰 Folha de Pagamento

### POST /api/folha/fechamento
Processar a folha mensal.
- **Response**
```json
{
  "mes": "08/2025",
  "totalFuncionarios": 25,
  "totalLiquido": 123456.78,
  "status": "consolidada"
}
```

### GET /api/folha/{mes}
Consultar folha por mês.
- **Response**
```json
{
  "mes": "08/2025",
  "funcionarios": [
    { "id": 1, "nome": "Maria Silva", "liquido": 4500.0 },
    { "id": 2, "nome": "João Souza", "liquido": 3700.0 }
  ]
}
```

---

## 📄 Contracheques

### GET /api/contracheques/{funcionarioId}/{mes}
Consultar contracheque de um funcionário.
- **Response**
```json
{
  "funcionarioId": 1,
  "mes": "08/2025",
  "proventos": 5000.0,
  "descontos": 500.0,
  "liquido": 4500.0
}
```

---

## 📌 Observações
- Todos os endpoints retornam **erros padronizados**:
```json
{
  "timestamp": "2025-08-10T10:00:00Z",
  "status": 400,
  "erro": "Dados inválidos",
  "mensagem": "CPF já cadastrado"
}
```
- A especificação completa deve ser registrada no arquivo [`openapi.yaml`](openapi.yaml) desta pasta.
