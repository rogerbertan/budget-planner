output "elastic_ip_nat" {
  value = aws_eip.nat.public_ip
}