resource "aws_iam_role" "ecs_task_execution_role" {
  name = "${local.project_name}-ecs-task-execution-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
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