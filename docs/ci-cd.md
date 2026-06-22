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
2. Baixa o artifact `tfplan` gerado pelo job `plan` (`actions/download-artifact`, com `path: infra`, já que o upload de um único arquivo remove o prefixo `infra/`).
3. `terraform init` e `terraform apply tfplan`.

Aplicar exatamente o arquivo de plano salvo no PR (em vez de gerar um plano novo no momento do apply) garante que o que foi revisado é, byte a byte, o que é executado, evitando drift entre a aprovação do PR e o merge.

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