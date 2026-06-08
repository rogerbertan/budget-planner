terraform {
  backend "s3" {
    bucket = "tfstate-backend-321289102277"
    key    = "aula/2026/terraform.tfstate"
    region = "us-east-1"
  }
}
