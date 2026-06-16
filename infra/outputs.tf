output "elastic_ip_nat" {
  value = aws_eip.nat.public_ip
}

output "ecr_repository_url" {
  value = aws_ecr_repository.budgetplanner-ecr.repository_url
}