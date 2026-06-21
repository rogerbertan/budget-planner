resource "aws_iam_role" "ecs_task_execution_role" {
  name = "${local.project_name}-ecs-task-execution-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Sid    = "AllowEcsTasksToAssumeRole",
        Effect = "Allow",
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_policy" "secret_access_policy" {
  name = "${local.project_name}-secret-access-policy"
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Sid    = "AllowToAccessSecrets",
        Effect = "Allow",
        Action = [
          "secretsmanager:GetSecretValue"
        ],
        Resource = aws_db_instance.budgetplanner-db.master_user_secret[0].secret_arn
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_role_policy" {
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
  role       = aws_iam_role.ecs_task_execution_role.id
}

resource "aws_iam_role_policy_attachment" "secret_access_policy_attachment" {
  policy_arn = aws_iam_policy.secret_access_policy.arn
  role       = aws_iam_role.ecs_task_execution_role.id
}

### GitHub OIDC

resource "aws_iam_role" "github_oidc_role" {
  name = "${local.project_name}-github-oidc-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Sid    = "AllowGitHubActionsToAssumeRole",
        Effect = "Allow",
        Principal = {
          Federated = aws_iam_openid_connect_provider.github_oidc.arn
        }
        Action = [
          "sts:AssumeRoleWithWebIdentity"
        ],
        Condition = {
          StringEquals = {
            "token.actions.githubusercontent.com:aud" = "sts.amazonaws.com"
          }
          StringLike = {
            "token.actions.githubusercontent.com:sub" = "repo:${var.github-repo}:ref:refs/heads/main"
          }
        }
      }
    ]
    }
  )
}

resource "aws_iam_policy" "github_oidc_policy" {
  name = "${local.project_name}-github-oidc-policy"
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Sid    = "AllowEcrAuth",
        Effect = "Allow",
        Action = [
          "ecr:GetAuthorizationToken"
        ],
        Resource = "*"
      },
      {
        Sid    = "AllowEcrPush",
        Effect = "Allow",
        Action = [
          "ecr:BatchCheckLayerAvailability",
          "ecr:BatchGetImage",
          "ecr:InitiateLayerUpload",
          "ecr:UploadLayerPart",
          "ecr:CompleteLayerUpload",
          "ecr:PutImage"
        ],
        Resource = aws_ecr_repository.budgetplanner-ecr.arn
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "github_oidc_role_policy_attachment" {
  policy_arn = aws_iam_policy.github_oidc_policy.arn
  role       = aws_iam_role.github_oidc_role.id
}