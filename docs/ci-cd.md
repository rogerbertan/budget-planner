# CI/CD - Budget Planner

Dois workflows do GitHub Actions cobrem o ciclo de vida do projeto: `.github/workflows/ci-cd.yml` (build, push da imagem e deploy da aplicação) e `.github/workflows/infra.yml` (plan/apply da infraestrutura Terraform).

## `infra.yml` - pipeline de infraestrutura

Trigger: qualquer push ou pull request para `main` que altere arquivos em `infra/**`.

### Job `plan` (em pull requests)

1. Autentica na AWS via OIDC.
2. `terraform init`, `fmt -check`, `validate`, `plan -out=tfplan`.
3. Salva o `tfplan` gerado como artifact (`actions/upload-artifact`).
4. Posta a saída do plano como comentário no PR via `actions/github-script`, atualizando o mesmo comentário em pushes seguintes (em vez de duplicar), usando um marcador HTML (`<!-- terraform-plan-comment -->`) para localizar o comentário anterior.

### Job `apply` (em push/merge na `main`)

1. Autentica na AWS via OIDC.
2. `terraform init` e `terraform plan -out=tfplan`.
3. `terraform apply tfplan`.

O job `apply` gera seu próprio plano em vez de reaproveitar o artifact `tfplan` do job `plan`. Isso porque os jobs `plan` (trigger `pull_request`) e `apply` (trigger `push`) acontecem em execuções (*workflow runs*) distintas do GitHub Actions, e artifacts não são compartilhados entre runs diferentes, um push direto na `main` sem PR associado nunca teria um artifact `terraform-plan` para baixar, causando `Artifact not found`. O preço dessa simplicidade é que o plano revisado no comentário do PR não é, byte a byte, o mesmo executado no apply (pode haver drift se algo mudar na AWS entre o merge e a execução do `apply`).

> Este projeto é de estudo, então o pipeline não usa `environment` com approval manual nem `concurrency` group antes do apply. Em um ambiente real, valeria adicionar as duas coisas: um *GitHub Environment* com required reviewers antes do `apply`, e um `concurrency: { group: infra-terraform }` para impedir dois applies simultâneos disputando o lock do state (o backend S3 deste projeto não usa DynamoDB para lock).

## O problema do bootstrap (ovo e galinha)

O pipeline `infra.yml` só funciona se duas coisas já existirem **antes** da primeira execução:

1. **O bucket S3 do state remoto** (`infra/backend.tf`), criado pelo Terraform em `infra/bootstrap/`, que usa state local, fora do backend S3 e fora do pipeline.
2. **O OIDC provider + a IAM role** que o GitHub Actions assume (`infra/github-oidc.tf`, `infra/iam.tf`), cujo ARN é salvo no secret `AWS_GITHUB_OIDC_ROLE_ARN`.

Sem essas duas peças, o workflow nem consegue autenticar na AWS, falhando já no primeiro `configure-aws-credentials`. Por isso, num computador/conta nova, **o pipeline não cria a infra inteiramente do zero**: o bootstrap inicial precisa ser feito manualmente, uma única vez, com credenciais locais (veja [docs/deploy.md](deploy.md)):

```
1. cd infra/bootstrap && terraform apply   # cria o bucket S3 do state
2. cd infra && terraform apply             # cria o resto, incluindo OIDC provider + IAM role
3. Salvar o ARN da role criada no passo 2 como secret AWS_GITHUB_OIDC_ROLE_ARN no GitHub
4. A partir daqui, o pipeline já consegue rodar plan/apply normalmente
```

### Permissões da role assumida via OIDC (`infra/iam.tf`)

A `aws_iam_role.github_oidc_role` só pode ser assumida (`sts:AssumeRoleWithWebIdentity`) quando o claim OIDC `sub` corresponde a `repo:<github-repo>:ref:refs/heads/main` (push na main) ou `repo:<github-repo>:pull_request` (qualquer pull request) — sem isso, o job `plan` (que roda em PRs) não autentica na AWS.

A `aws_iam_policy.github_oidc_policy` dá a essa role, além das permissões de ECR/ECS/`iam:PassRole` usadas pelo deploy da aplicação, acesso de leitura/escrita ao objeto do state remoto (`s3:GetObject`, `s3:PutObject`, `s3:ListBucket` no bucket `tfstate-backend-321289102277`), necessário para o `terraform init`/`plan`/`apply` lerem e atualizarem o state. Sem essa permissão, o `terraform init` falha com `403 Forbidden` ao tentar acessar `budgetplanner/state/terraform.tfstate`.

Além dessa policy customizada, a role tem policies gerenciadas da AWS anexadas (`AmazonEC2FullAccess`, `ElasticLoadBalancingFullAccess`, `AmazonECS_FullAccess`, `AmazonEC2ContainerRegistryFullAccess`, `IAMFullAccess`, `CloudWatchLogsFullAccess`, `CloudWatchFullAccess`, `AmazonSNSFullAccess`, `AmazonRDSFullAccess`), necessárias para o `terraform plan`/`apply` conseguir ler e gerenciar todos os recursos presentes no state.