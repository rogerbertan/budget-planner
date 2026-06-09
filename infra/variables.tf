variable "bucket_backend_name" {
  default     = "tfstate-backend-321289102277"
  description = "Nome do bucket S3 para armazenar o estado do Terraform."
}

variable "aws_region" {
  default     = "us-east-1"
  description = "Região da AWS onde os recursos serão provisionados."
}

variable "vpc_cidr_block" {
  default     = "10.0.0.0/16"
  description = "CIDR block para a VPC."
}

variable "cidr_block_private" {
  default     = "10.0.1.0/24"
  description = "CIDR block para a sub-rede privada."
}

variable "cidr_block_public" {
  default     = "10.0.2.0/24"
  description = "CIDR block para a sub-rede pública."
}