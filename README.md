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

1. Build e push da imagem para o ECR (crie o repositório antes, se ainda não existir)
   ```bash
   aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com
   docker build -t budgetplanner-ecr .
   docker tag budgetplanner-ecr:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/budgetplanner-ecr:<tag>
   docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/budgetplanner-ecr:<tag>
   ```

2. Provisione a infraestrutura
   ```bash
   cd infra
   terraform init
   terraform apply -var="image_tag=<tag>"
   ```

O estado do Terraform é armazenado em S3 (backend configurado em `infra/backend.tf`). Há também um módulo `infra/bootstrap` para criar esse bucket de estado, caso ainda não exista.

Ao final, a API estará disponível pela URL do output `alb_dns_name`.

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