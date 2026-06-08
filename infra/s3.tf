resource "aws_s3_bucket" "bucket-backend" {
  bucket = "tfstate-backend-321289102277"

  tags = {
    Name        = "tfstate"
    Environment = "Production"
  }
}

resource "aws_s3_bucket" "bucket-aula" {
  bucket = "bucket-aula-321289102277"

  tags = {
    Name        = "aula"
    Environment = "Develop"
  }
}