resource "aws_db_instance" "budgetplanner-db" {
  allocated_storage           = 10
  db_name                     = "budgetplannerdb"
  engine                      = "postgres"
  engine_version              = "18.4"
  instance_class              = "db.t4g.micro"
  username                    = "postgres"
  manage_master_user_password = true
  parameter_group_name        = "default.postgres18"
  skip_final_snapshot         = true
  db_subnet_group_name        = aws_db_subnet_group.db_subnet_group.name
  vpc_security_group_ids      = [aws_security_group.rds.id]
}