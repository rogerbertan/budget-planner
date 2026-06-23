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

Infraestrutura: ECR + ECS Fargate + RDS Postgres + ALB, gerenciada em `infra/`. O setup inicial é feito via Terraform; a partir daí, cada push em `main` aciona o pipeline de CI/CD (`.github/workflows/ci-cd.yml`), que builda, testa, publica a imagem no ECR e atualiza o serviço ECS automaticamente.

Observabilidade via CloudWatch e SNS, detalhada em [Decisões Técnicas](#monitoramento-com-cloudwatch-e-alarme-de-5xx-via-sns).

Guia completo passo a passo na [documentação de Deploy](docs/deploy.md).

Pipeline de CI/CD (build, deploy da app e plan/apply da infra) documentado em [documentação de Pipelines](docs/ci-cd.md).

## Decisões Técnicas

### Por que OIDC e não Access Key no CI/CD?

O pipeline de CD autentica na AWS via OIDC em vez de um Access Key fixo guardado como secret. O Access Key, uma vez salvo, fica válido indefinidamente até ser revogado manualmente, então se vazar o risco persiste até alguém notar. O OIDC troca um token temporário emitido pelo GitHub por credenciais da AWS que expiram em minutos, geradas a cada execução, sem nenhum segredo fixo armazenado, além de permitir restringir via trust policy quem pode assumir a IAM Role (ex: só a branch `main` deste repositório).

Resumo: OIDC foi escolhido pela ausência de segredo de longa duração e por restringir o acesso por repositório e branch, sendo também a prática hoje recomendada pela AWS e pelo GitHub.

### Monitoramento com CloudWatch e alarme de 5xx via SNS

Os logs do container são enviados para um Log Group do CloudWatch (`aws_cloudwatch_log_group.budgetplanner_log_group`, em `infra/cloudwatch.tf`), com retenção de 14 dias, evitando que se percam quando a task reinicia.

Um alarme (`aws_cloudwatch_metric_alarm.alb_5xx`) monitora a métrica `HTTPCode_Target_5XX_Count` do ALB e dispara quando há mais de 5 respostas 5xx em 1 minuto. O alarme notifica um tópico SNS (`aws_sns_topic.alarms`), que envia email para o endereço configurado em `alarm_email`. Após o `apply`, é necessário confirmar a inscrição pelo link enviado por email para que as notificações sejam entregues.

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