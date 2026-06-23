# Observabilidade - Budget Planner

Observabilidade via CloudWatch e SNS, provisionada em `infra/cloudwatch.tf`: logs do container, alarme de erros 5xx no ALB e um dashboard centralizando as métricas de ECS, ALB e RDS.

## Logs

Os logs do container (stdout/stderr) são enviados para um Log Group do CloudWatch (`aws_cloudwatch_log_group.budgetplanner_log_group`), referenciado no `logConfiguration` da task definition (`infra/ecs.tf`):

- Nome: `/ecs/budgetplanner`
- Retenção: 14 dias

Sem essa configuração, os logs só existiriam enquanto a task estivesse rodando, sendo perdidos a cada restart ou deploy.

## Alarme de erros 5xx

O alarme `aws_cloudwatch_metric_alarm.alb_5xx` monitora a métrica `HTTPCode_Target_5XX_Count` (namespace `AWS/ApplicationELB`) e dispara quando há mais de 5 respostas 5xx em um período de 1 minuto.

Pontos da implementação:

- As dimensões usam `arn_suffix` do load balancer e do target group, formato exigido pela métrica do ALB.
- `treat_missing_data = "notBreaching"` evita falso alarme quando não há tráfego no período avaliado.
- `ok_actions` notifica quando o alarme volta ao normal, sem precisar checar a console manualmente.

## Notificação via SNS

O alarme notifica o tópico SNS `aws_sns_topic.alarms`, que envia email para o endereço configurado na variável `alarm_email` (`infra/variables.tf`).

Após o `apply`, a AWS envia um email de confirmação de inscrição (subscription) para esse endereço. As notificações só são entregues depois que o link de confirmação é aceito.

## Dashboard

O dashboard `aws_cloudwatch_dashboard.budgetplanner` centraliza as métricas mais relevantes da infraestrutura em uma única tela:

- **ECS**: CPU e Memória do cluster/service.
- **ALB**: RequestCount e TargetResponseTime (latência).
- **ALB**: Erros 5xx, HealthyHostCount e UnHealthyHostCount.
- **RDS**: CPU, DatabaseConnections e FreeStorageSpace.

Acesso: AWS Console → CloudWatch → Dashboards → `budgetplanner-dashboard`.

![Dashboard CloudWatch](img/cloudwatch-dashboard.png)

## Custos

Para o volume desta aplicação (2 tasks Fargate, 1 ALB, 1 RDS `db.t4g.micro`), o custo de observabilidade é marginal:

| Item | Custo aproximado |
|---|---|
| Métricas nativas (ECS, ALB, RDS) | Gratuitas |
| Logs (ingestão + armazenamento, retenção de 14 dias) | Centavos a poucos dólares/mês |
| Alarme (`alb_5xx`) | ~US$0,10/mês |
| SNS (notificações por email) | Gratuito até 1000 notificações/mês |
| Dashboard customizado | Gratuito (até 3 dashboards por conta) |

Estimativa total: menos de US$5/mês.