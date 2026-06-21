# Guia de Deploy - Budget Planner

Deploy na AWS: setup inicial da infraestrutura é manual (via Terraform); build, push da imagem e atualização do serviço ECS são automáticos via CI/CD a cada push em `main`.

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

## 3. Deploy da infraestrutura (`infra/`)

```bash
cd infra

terraform init
terraform apply
```

Recursos criados:
- VPC com subnets públicas e privadas (`us-east-1a` e `us-east-1b`) + Internet Gateway + NAT Gateway
- ECR repository (`budgetplanner-ecr`)
- ECS Cluster + Task Definition + Service Fargate (2 tasks, subnet privada)
- Application Load Balancer (subnet pública) + Target Group com health check em `/actuator/health`
- RDS PostgreSQL (`db.t4g.micro`), com senha gerenciada automaticamente via Secrets Manager (`manage_master_user_password`)
- IAM Role de execução das tasks ECS, com permissão de leitura do secret do RDS
- IAM OIDC Identity Provider + IAM Role para o GitHub Actions assumir via OIDC, usada pelo pipeline de CI/CD para publicar imagens no ECR e atualizar o serviço ECS

Na primeira execução, o ECS sobe com a tag definida em `image_tag` (`infra/variables.tf`). A partir daí, cada push em `main` publica uma imagem nova e promove o serviço automaticamente (ver seção [Deploy automático via CI/CD](#deploy-automático-via-cicd)).

---

## 4. Verificar a aplicação

Ao final do `apply`, o Terraform expõe os outputs:

```bash
terraform output alb_dns_name
terraform output ecr_repository_url
terraform output github_oidc_role_arn
```

Testar o health check pela URL do ALB:

```bash
ALB_URL=$(terraform output -raw alb_dns_name)
curl http://${ALB_URL}/actuator/health
```

---

## Deploy automático via CI/CD

O workflow `.github/workflows/ci-cd.yml` cuida do deploy da aplicação a cada push em `main` (PRs só rodam os testes, sem tocar em AWS). O pipeline tem 3 jobs em sequência:

1. **`build-and-test`** — `mvn verify` (build + testes). Roda em todo push e pull request.
2. **`build-and-push`** — builda a imagem Docker e publica no ECR, com a tag `${{ github.sha }}` (única por commit, compatível com o repositório `IMMUTABLE`). Autentica na AWS via OIDC, sem nenhuma credencial de longa duração armazenada como secret.
3. **`deploy`** — busca a task definition atual (`describe-task-definition`), atualiza o campo `image` para a tag publicada no passo anterior, registra uma nova revisão (`register-task-definition`) e atualiza o serviço ECS para usá-la (`update-service`).

Os jobs 2 e 3 só rodam em push direto para `main` (`if: github.event_name == 'push'`), nunca em pull request.

### Autenticação via OIDC

A autenticação usa um IAM OIDC Identity Provider (`infra/github-oidc.tf`) e uma IAM Role (`infra/iam.tf`, recurso `github_oidc_role`) com trust policy restrita ao repositório e à branch `main`. O ARN dessa role é guardado no secret `AWS_GITHUB_OIDC_ROLE_ARN` do repositório no GitHub (Settings → Secrets and variables → Actions), e referenciado no workflow.

A policy de permissões anexada à role (`github_oidc_policy`) cobre apenas o necessário: autenticação e push no ECR, leitura/registro de task definitions do ECS, atualização do serviço ECS e `iam:PassRole` para a role de execução das tasks — escopadas aos recursos específicos deste projeto sempre que a API do ECS/ECR permite.

---

## Troubleshooting

| Problema                                                  | Causa                                                         | Solução                                                                                      |
|-----------------------------------------------------------|---------------------------------------------------------------|----------------------------------------------------------------------------------------------|
| `terraform init` falha com erro de bucket inexistente     | Bucket de state ainda não foi criado                          | Rodar `infra/bootstrap` antes (passo 2)                                                      |
| `docker push` falha com `ImageTagAlreadyExists`           | Repositório ECR é `IMMUTABLE`                                 | Não ocorre no fluxo automático (tag é o `github.sha`, sempre único por commit)               |
| Job `deploy` falha com `AccessDeniedException`            | Falta alguma permissão na policy `github_oidc_policy`         | Verificar a ação/recurso exato no erro e ajustar `infra/iam.tf`                               |
| Tasks ECS não ficam `RUNNING` / health check falha no ALB | Endpoint `/actuator/health` não responde                      | Validar se a imagem builda corretamente e expõe a porta 8080                                 |
| ECS não consegue ler `DB_USER`/`DB_PASSWORD`              | Secret do RDS ainda não propagou ou policy não anexada        | Verificar `aws_iam_role_policy_attachment.secret_access_policy_attachment` em `infra/iam.tf` |
| `terraform apply` trava no NAT Gateway/RDS                | Provisionamento naturalmente lento (alguns minutos)           | Aguardar; RDS e NAT Gateway levam mais tempo que os demais recursos                          |
| Conexão com o banco recusada                              | Security Group do RDS só libera a porta 5432 para o SG do ECS | Não há acesso externo direto ao RDS por design (`infra/networks.tf`)                         |
