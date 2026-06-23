resource "aws_cloudwatch_log_group" "budgetplanner_log_group" {
  name              = "/ecs/${local.project_name}"
  retention_in_days = 14

  tags = {
    Name = "${local.project_name}-log-group"
  }
}
