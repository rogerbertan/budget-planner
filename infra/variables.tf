variable "bucket_backend_name" {
  default     = "tfstate-backend-321289102277"
  description = "Nome do bucket S3 para armazenar o estado do Terraform."
}

variable "aws_region" {
  default     = "us-east-1"
  description = "Região da AWS onde os recursos serão provisionados."
}

variable "image_tag" {
  default     = "1.0.0"
  description = "Tag da imagem Docker."
}

variable "vpc_cidr_block" {
  default     = "10.0.0.0/16"
  description = "CIDR block para a VPC."
}

variable "private-subnets" {
  default = {
    "us-east-1a" = "10.0.1.0/24"
    "us-east-1b" = "10.0.3.0/24"
  }
  description = "Mapa de sub-redes privadas por zona de disponibilidade."
}

variable "public-subnets" {
  default = {
    "us-east-1a" = "10.0.2.0/24"
    "us-east-1b" = "10.0.4.0/24"
  }
  description = "Mapa de sub-redes públicas por zona de disponibilidade."
}