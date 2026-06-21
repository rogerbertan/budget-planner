output "ecr_repository_url" {
  value = aws_ecr_repository.budgetplanner-ecr.repository_url
}

output "alb_dns_name" {
  value = aws_lb.application_load_balancer.dns_name
}

output "github_oidc_role_arn" {
  value = aws_iam_role.github_oidc_role.arn
}