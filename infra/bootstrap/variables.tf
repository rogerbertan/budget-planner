variable "bucket_backend_name" {
  default     = "tfstate-backend-321289102277"
  description = "Nome do bucket S3 para armazenar o estado do Terraform."
}

variable "aws_region" {
  default     = "us-east-1"
  description = "Região da AWS onde os recursos serão provisionados."
}