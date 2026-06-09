terraform {
  backend "s3" {
    bucket = "tfstate-backend-321289102277"
    key    = "budgetplanner/state/terraform.tfstate"
    region = "us-east-1"
  }
}
