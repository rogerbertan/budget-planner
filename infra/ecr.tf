resource "aws_ecr_repository" "budgetplanner-ecr" {
  name                 = "${local.project_name}-ecr"
  image_tag_mutability = "IMMUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name = "${local.project_name}-ecr"
  }
}
