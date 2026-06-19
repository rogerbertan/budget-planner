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
  container_definitions = jsonencode(
    [{
      name      = "${local.project_name}-container"
      image     = aws_ecr_repository.budgetplanner-ecr.repository_url
      essential = true
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
  iam_role        = aws_iam_role.ecs_task_execution_role.id

  load_balancer {
    target_group_arn = aws_lb_target_group.budgetplanner-tg.arn
    container_name   = "${local.project_name}-container"
    container_port   = 8080
  }

  tags = {
    Name = "${local.project_name}-service"
  }
}