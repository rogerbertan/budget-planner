resource "aws_s3_bucket" "bucket-backend" {
  bucket = var.bucket_backend_name

  tags = {
    Name        = "tfstate"
    Environment = "Production"
  }
}