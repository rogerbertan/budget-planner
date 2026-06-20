# Guia de Deploy - Budget Planner

Deploy manual completo na AWS, do zero.

## Pré-requisitos

```bash 
# Verificar ferramentas instaladas
aws --version       # AWS CLI v2
terraform --version # >= 1.15.0
docker --version
```

## 1. Configurar credenciais AWS

Configure suas credenciais em `~/.aws/credentials`:

```ini
[default]
aws_access_key_id = ...
aws_secret_access_key = ...
```

Verificar:
```bash
aws sts get-caller-identity
```

Exportar a região usada pelo projeto:
```bash
export AWS_DEFAULT_REGION=us-east-1
```

---

## 2. Criar bucket S3 para o Terraform state

Precisa ser criado apenas uma vez. O módulo `infra/bootstrap` provisiona esse bucket:

```bash
cd infra/bootstrap

terraform init
terraform apply
```

Isso cria o bucket `tfstate-backend-<account-id>`, referenciado em `infra/backend.tf`. Se o bucket já existir (ex: em deploys anteriores), pule esta etapa.

---

## 3. Build e push da imagem para o ECR

O repositório ECR (`budgetplanner-ecr`) é criado pelo próprio Terraform do passo 4. Se ainda não existir, crie-o manualmente antes do primeiro push, ou rode `terraform apply` do passo 4 primeiro e depois volte aqui.

```bash
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
ECR_REGISTRY="${ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com"
TAG=1.0.0

aws ecr get-login-password --region us-east-1 \
  | docker login --username AWS --password-stdin ${ECR_REGISTRY}

docker build -t budgetplanner-ecr .
docker tag budgetplanner-ecr:latest ${ECR_REGISTRY}/budgetplanner-ecr:${TAG}
docker push ${ECR_REGISTRY}/budgetplanner-ecr:${TAG}
```

> O repositório ECR usa `image_tag_mutability = "IMMUTABLE"` — uma tag já publicada não pode ser sobrescrita. Use uma tag nova a cada novo build (ex: `1.0.1`).

---

## 4. Deploy da infraestrutura (`infra/`)

```bash
cd infra

terraform init
terraform apply -var="image_tag=<TAG>"
```

Recursos criados:
- VPC com subnets públicas e privadas (`us-east-1a` e `us-east-1b`) + Internet Gateway + NAT Gateway
- ECR repository (`budgetplanner-ecr`)
- ECS Cluster + Task Definition + Service Fargate (2 tasks, subnet privada)
- Application Load Balancer (subnet pública) + Target Group com health check em `/actuator/health`
- RDS PostgreSQL (`db.t4g.micro`), com senha gerenciada automaticamente via Secrets Manager (`manage_master_user_password`)
- IAM Role de execução das tasks ECS, com permissão de leitura do secret do RDS

> A tag de imagem usada no deploy é a mesma publicada no passo 3 (`image_tag`, default `1.0.0` em `infra/variables.tf`).

---

## 5. Verificar a aplicação

Ao final do `apply`, o Terraform expõe os outputs:

```bash
terraform output alb_dns_name
terraform output ecr_repository_url
```

Testar o health check pela URL do ALB:

```bash
ALB_URL=$(terraform output -raw alb_dns_name)
curl http://${ALB_URL}/actuator/health
```

---

## Ordem de deploy resumida

```
1. ~/.aws/credentials + export AWS_DEFAULT_REGION=us-east-1
2. infra/bootstrap        (bucket S3 do state, só na primeira vez)
3. docker build/push       (imagem para o ECR)
4. infra (terraform apply) (VPC, ECS, ALB, RDS)
5. curl no alb_dns_name    (verificar health)
```

---

## Troubleshooting

| Problema                                                  | Causa                                                         | Solução                                                                                      |
|-----------------------------------------------------------|---------------------------------------------------------------|----------------------------------------------------------------------------------------------|
| `terraform init` falha com erro de bucket inexistente     | Bucket de state ainda não foi criado                          | Rodar `infra/bootstrap` antes (passo 2)                                                      |
| `docker push` falha com `ImageTagAlreadyExists`           | Repositório ECR é `IMMUTABLE`                                 | Usar uma tag nova e reaplicar com `-var="image_tag=<nova-tag>"`                              |
| Tasks ECS não ficam `RUNNING` / health check falha no ALB | Endpoint `/actuator/health` não responde                      | Validar se a imagem builda corretamente e expõe a porta 8080                                 |
| ECS não consegue ler `DB_USER`/`DB_PASSWORD`              | Secret do RDS ainda não propagou ou policy não anexada        | Verificar `aws_iam_role_policy_attachment.secret_access_policy_attachment` em `infra/iam.tf` |
| `terraform apply` trava no NAT Gateway/RDS                | Provisionamento naturalmente lento (alguns minutos)           | Aguardar; RDS e NAT Gateway levam mais tempo que os demais recursos                          |
| Conexão com o banco recusada                              | Security Group do RDS só libera a porta 5432 para o SG do ECS | Não há acesso externo direto ao RDS por design (`infra/networks.tf`)                         |
