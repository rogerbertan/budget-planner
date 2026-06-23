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