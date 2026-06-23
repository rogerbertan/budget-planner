resource "aws_cloudwatch_log_group" "budgetplanner_log_group" {
  name              = "/ecs/${local.project_name}"
  retention_in_days = 14

  tags = {
    Name = "${local.project_name}-log-group"
  }
}

resource "aws_cloudwatch_metric_alarm" "alb_5xx" {
  alarm_name          = "${local.project_name}-alb-5xx"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "HTTPCode_Target_5XX_Count"
  namespace           = "AWS/ApplicationELB"
  period              = 60
  statistic           = "Sum"
  threshold           = 5
  alarm_description   = "Dispara quando o ALB retorna mais de 5 erros 5xx em um minuto."
  treat_missing_data  = "notBreaching"

  dimensions = {
    LoadBalancer = aws_lb.application_load_balancer.arn_suffix
    TargetGroup  = aws_lb_target_group.budgetplanner-tg.arn_suffix
  }

  alarm_actions = [aws_sns_topic.alarms.arn]
  ok_actions    = [aws_sns_topic.alarms.arn]
}

### SNS para alarmes CloudWatch

resource "aws_sns_topic" "alarms" {
  name = "${local.project_name}-alarms"

  tags = {
    Name = "${local.project_name}-alarms"
  }
}

resource "aws_sns_topic_subscription" "alarms_email" {
  topic_arn = aws_sns_topic.alarms.arn
  protocol  = "email"
  endpoint  = var.alarm_email
}

### Dashboard centralizando métricas de ECS, ALB e RDS

resource "aws_cloudwatch_dashboard" "budgetplanner" {
  dashboard_name = "${local.project_name}-dashboard"

  dashboard_body = jsonencode({
    widgets = [
      {
        type   = "metric"
        x      = 0
        y      = 0
        width  = 12
        height = 6
        properties = {
          title  = "ECS - CPU e Memória"
          region = var.aws_region
          stat   = "Average"
          period = 60
          metrics = [
            ["AWS/ECS", "CPUUtilization", "ClusterName", aws_ecs_cluster.budgetplanner_cluster.name, "ServiceName", aws_ecs_service.budgetplanner_service.name],
            ["AWS/ECS", "MemoryUtilization", "ClusterName", aws_ecs_cluster.budgetplanner_cluster.name, "ServiceName", aws_ecs_service.budgetplanner_service.name]
          ]
        }
      },
      {
        type   = "metric"
        x      = 12
        y      = 0
        width  = 12
        height = 6
        properties = {
          title  = "ALB - Requisições e Latência"
          region = var.aws_region
          period = 60
          metrics = [
            ["AWS/ApplicationELB", "RequestCount", "LoadBalancer", aws_lb.application_load_balancer.arn_suffix, { stat = "Sum" }],
            ["AWS/ApplicationELB", "TargetResponseTime", "LoadBalancer", aws_lb.application_load_balancer.arn_suffix, { stat = "Average", yAxis = "right" }]
          ]
        }
      },
      {
        type   = "metric"
        x      = 0
        y      = 6
        width  = 12
        height = 6
        properties = {
          title  = "ALB - Erros 5xx e Hosts Saudáveis"
          region = var.aws_region
          period = 60
          metrics = [
            ["AWS/ApplicationELB", "HTTPCode_Target_5XX_Count", "LoadBalancer", aws_lb.application_load_balancer.arn_suffix, "TargetGroup", aws_lb_target_group.budgetplanner-tg.arn_suffix, { stat = "Sum" }],
            ["AWS/ApplicationELB", "HealthyHostCount", "LoadBalancer", aws_lb.application_load_balancer.arn_suffix, "TargetGroup", aws_lb_target_group.budgetplanner-tg.arn_suffix, { stat = "Average", yAxis = "right" }],
            ["AWS/ApplicationELB", "UnHealthyHostCount", "LoadBalancer", aws_lb.application_load_balancer.arn_suffix, "TargetGroup", aws_lb_target_group.budgetplanner-tg.arn_suffix, { stat = "Average", yAxis = "right" }]
          ]
        }
      },
      {
        type   = "metric"
        x      = 12
        y      = 6
        width  = 12
        height = 6
        properties = {
          title  = "RDS - CPU, Conexões e Storage Livre"
          region = var.aws_region
          period = 60
          metrics = [
            ["AWS/RDS", "CPUUtilization", "DBInstanceIdentifier", aws_db_instance.budgetplanner-db.id, { stat = "Average" }],
            ["AWS/RDS", "DatabaseConnections", "DBInstanceIdentifier", aws_db_instance.budgetplanner-db.id, { stat = "Average" }],
            ["AWS/RDS", "FreeStorageSpace", "DBInstanceIdentifier", aws_db_instance.budgetplanner-db.id, { stat = "Average", yAxis = "right" }]
          ]
        }
      }
    ]
  })
}