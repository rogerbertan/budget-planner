# Budget Planner

Uma API REST para registrar receitas e despesas pessoais, com categorias e resumos financeiros.

---

## Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Tecnologias](#tecnologias)
- [Como Começar](#como-começar)
- [Uso](#uso)
- [Roadmap](#roadmap)
- [Contribuindo](#contribuindo)
- [Licença](#licença)

---

## Sobre o Projeto

O Budget Planner é uma API REST construída com Spring Boot para gerenciar finanças pessoais. Com ele é possível categorizar transações como receita ou despesa, consultar o histórico paginado de transações e obter resumos financeiros - saldo geral, resumo mensal e totais por categoria.

O esquema do banco de dados é versionado com Flyway e o projeto inclui uma configuração Docker Compose para rodar tudo com um único comando.

---

## Tecnologias

- Java 21
- Spring Boot 4
- PostgreSQL 16
- Flyway
- Docker Compose
- Maven

---

## Como Começar

### Opção 1 - Docker Compose (recomendado)

**Pré-requisitos:** Docker e Docker Compose

1. Clone o repositório
   ```bash
   git clone https://github.com/rogerbertan/budget-planner.git
   cd budget-planner
   ```

2. Copie o arquivo de exemplo e preencha os valores
   ```bash
   cp .env.example .env
   ```

   ```env
   POSTGRES_DB=budget-planner
   POSTGRES_USER=postgres
   POSTGRES_PASSWORD=postgres
   ```

3. Inicie a aplicação e o banco de dados
   ```bash
   docker compose up --build
   ```

### Opção 2 - Maven (local)

**Pré-requisitos:** Java 21, Maven, PostgreSQL

1. Clone o repositório
   ```bash
   git clone https://github.com/rogerbertan/budget-planner.git
   cd budget-planner
   ```

2. Crie o banco de dados
   ```bash
   createdb budget-planner
   ```

3. Defina as variáveis de ambiente
   ```bash
   export DB_URL=jdbc:postgresql://localhost:5432/budget-planner
   export DB_USER=postgres
   export DB_PASSWORD=postgres
   ```

4. Inicie a aplicação
   ```bash
   ./mvnw spring-boot:run
   ```

A API estará disponível em `http://localhost:8080`.

---

## Uso

### Health Check

| Método | Rota      | Descrição                    |
|--------|-----------|------------------------------|
| `GET`  | `/health` | Verifica se a API está no ar |

```json
{ "status": "UP" }
```

---

### Categorias

| Método   | Rota               | Descrição                 |
|----------|--------------------|---------------------------|
| `GET`    | `/categories`      | Lista todas as categorias |
| `POST`   | `/categories`      | Cria uma categoria        |
| `PUT`    | `/categories/{id}` | Atualiza uma categoria    |
| `DELETE` | `/categories/{id}` | Remove uma categoria      |

O campo `type` aceita `INCOME` ou `EXPENSE`.

**POST /categories - requisição**
```json
{ "name": "Mercado", "type": "EXPENSE" }
```

**POST /categories - resposta `201`**
```json
{ "id": 1, "name": "Mercado", "type": "EXPENSE" }
```

---

### Transações

| Método   | Rota                 | Descrição                                                  |
|----------|----------------------|------------------------------------------------------------|
| `GET`    | `/transactions`      | Lista transações (paginado, ordenado por data decrescente) |
| `GET`    | `/transactions/{id}` | Busca uma transação específica                             |
| `POST`   | `/transactions`      | Cria uma transação                                         |
| `PUT`    | `/transactions/{id}` | Atualiza uma transação                                     |
| `DELETE` | `/transactions/{id}` | Remove uma transação                                       |

**POST /transactions - requisição**
```json
{
  "type": "EXPENSE",
  "amount": 150.50,
  "description": "Compras da semana",
  "categoryId": 1,
  "transactionDate": "2024-01-15"
}
```

**POST /transactions - resposta `201`**
```json
{
  "id": 1,
  "type": "EXPENSE",
  "amount": 150.50,
  "description": "Compras da semana",
  "categoryId": 1,
  "transactionDate": "2024-01-15",
  "createdAt": "2024-01-15T10:30:00"
}
```

**GET /transactions - resposta**
```json
{
  "content": [{ "id": 1, "...": "..." }],
  "pageable": { "pageNumber": 0, "pageSize": 20 },
  "totalElements": 1,
  "totalPages": 1
}
```

---

### Resumos

| Método | Rota                                     | Descrição                                    |
|--------|------------------------------------------|----------------------------------------------|
| `GET`  | `/summary/balance`                       | Saldo geral com total de receitas e despesas |
| `GET`  | `/summary/monthly?month={m}&year={y}`    | Resumo de um mês específico                  |
| `GET`  | `/summary/categories?month={m}&year={y}` | Totais agrupados por categoria               |

**GET /summary/balance**
```json
{ "totalIncome": 5000.00, "totalExpense": 3200.50, "balance": 1799.50 }
```

**GET /summary/monthly?month=1&year=2024**
```json
{ "month": 1, "year": 2024, "totalIncome": 5000.00, "totalExpense": 3200.50, "balance": 1799.50 }
```

**GET /summary/categories?month=1&year=2024**
```json
[
  { "categoryId": 1, "categoryName": "Mercado",  "type": "EXPENSE", "total": 450.00 },
  { "categoryId": 2, "categoryName": "Salário",  "type": "INCOME",  "total": 5000.00 }
]
```

---

## Roadmap

- [x] CRUD de categorias
- [x] CRUD de transações com paginação
- [x] Resumos financeiros (saldo, mensal e por categoria)
- [x] Configuração com Docker Compose
- [ ] Autenticação e suporte a múltiplos usuários
- [ ] Exportação de transações para CSV

Veja as [issues abertas](https://github.com/rogerbertan/budget-planner/issues) para a lista completa de melhorias propostas.

---

## Contribuindo

Contribuições são bem-vindas. Faça um fork do repositório e abra um pull request, ou abra uma issue com a tag `enhancement`.

1. Faça um fork do projeto
2. Crie sua branch (`git checkout -b feature/MinhaFeature`)
3. Faça o commit das suas alterações (`git commit -m 'Add MinhaFeature'`)
4. Envie para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

---

## Licença

Distribuído sob a licença MIT. Veja o arquivo `LICENSE` para mais informações.