# Budget Planner

API REST para controle de finanças pessoais: registra receitas e despesas, organiza por categoria e mostra resumos (saldo, mensal, por categoria).

## Problema que resolve

Substitui planilha manual por uma API simples que centraliza transações financeiras e calcula saldo/resumos automaticamente, com histórico paginado e categorização.

## Tecnologias

Java 21 · Spring Boot 4 · PostgreSQL 16 · Flyway · Maven · Docker · Terraform (AWS)

## Rodando local com Docker Compose

```bash
cp .env.example .env
# edite .env com as credenciais do banco
docker compose up --build
```

API disponível em `http://localhost:8080`.

## Rodando local com Maven (sem Docker)

```bash
createdb budget-planner

export DB_URL=jdbc:postgresql://localhost:5432/budget-planner
export DB_USER=postgres
export DB_PASSWORD=postgres

./mvnw spring-boot:run
```

## Deploy na AWS com Terraform

Infraestrutura: ECR + ECS Fargate + RDS Postgres + ALB, gerenciada em `infra/`.

Guia completo passo a passo na [documentação de Deploy](docs/deploy.md).

## Endpoints principais

- `GET /health` — status da API
- `GET/POST/PUT/DELETE /categories` — categorias (tipo `INCOME` ou `EXPENSE`)
- `GET/POST/PUT/DELETE /transactions` — transações (paginado)
- `GET /summary/balance` — saldo geral
- `GET /summary/monthly?month=&year=` — resumo mensal
- `GET /summary/categories?month=&year=` — totais por categoria

## Roadmap

- [x] CRUD de categorias e transações
- [x] Resumos financeiros
- [x] Docker Compose e deploy via Terraform na AWS
- [ ] Autenticação e múltiplos usuários
- [ ] Exportação de transações para CSV