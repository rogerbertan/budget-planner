resource "aws_ecs_cluster" "budgetplanner_cluster" {
  name = "${local.project_name}-cluster"

  tags = {
    Name = "${local.project_name}-cluster"
  }
}

resource "aws_ecs_task_definition" "budgetplanner_task" {
  family                   = "${local.project_name}-task"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  container_definitions = jsonencode(
    [{
      name      = "${local.project_name}-container"
      image     = aws_ecr_repository.budgetplanner-ecr.repository_url
      essential = true
      environment = [
        {
          name  = "DB_URL"
          value = "jdbc:postgresql://${aws_db_instance.budgetplanner-db.address}:5432/${aws_db_instance.budgetplanner-db.db_name}"
        }
      ]
      secrets = [
        {
          name      = "DB_USER"
          valueFrom = "${aws_db_instance.budgetplanner-db.master_user_secret[0].secret_arn}:username::"
        },
        {
          name      = "DB_PASSWORD"
          valueFrom = "${aws_db_instance.budgetplanner-db.master_user_secret[0].secret_arn}:password::"
        }
      ]
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
          protocol      = "tcp"
        }
      ]
      }
  ])

  tags = {
    Name = "${local.project_name}-task"
  }
}

resource "aws_ecs_service" "budgetplanner_service" {
  name            = "${local.project_name}-service"
  cluster         = aws_ecs_cluster.budgetplanner_cluster.id
  task_definition = aws_ecs_task_definition.budgetplanner_task.id
  desired_count   = 2
  launch_type     = "FARGATE"

  network_configuration {
    subnets         = [for subnet in aws_subnet.private : subnet.id]
    security_groups = [aws_security_group.ecs.id]
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.budgetplanner-tg.arn
    container_name   = "${local.project_name}-container"
    container_port   = 8080
  }

  tags = {
    Name = "${local.project_name}-service"
  }
}