resource "aws_vpc" "budgetplanner-vpc" {
  cidr_block           = var.vpc_cidr_block
  instance_tenancy     = "default"
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "${local.project_name}-vpc"
  }
}

resource "aws_internet_gateway" "gw" {
  vpc_id = aws_vpc.budgetplanner-vpc.id

  tags = {
    Name = "${local.project_name}-igw"
  }
}

resource "aws_eip" "nat" {}

### PRIVATE

resource "aws_subnet" "private" {
  for_each          = var.private-subnets
  vpc_id            = aws_vpc.budgetplanner-vpc.id
  cidr_block        = each.value
  availability_zone = each.key

  tags = {
    Name = "${local.project_name}-private-subnet-${each.key}"
  }
}

resource "aws_route_table" "private_rt" {
  vpc_id = aws_vpc.budgetplanner-vpc.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.public.id
  }

  tags = {
    Name = "${local.project_name}-private-rt"
  }
}

resource "aws_route_table_association" "private" {
  for_each       = aws_subnet.private
  subnet_id      = each.value.id
  route_table_id = aws_route_table.private_rt.id
}

resource "aws_db_subnet_group" "db_subnet_group" {
  name       = "${local.project_name}-db-subnet-group"
  subnet_ids = [aws_subnet.private["us-east-1a"].id, aws_subnet.private["us-east-1b"].id]

  tags = {
    Name = "${local.project_name}-db-subnet-group"
  }
}

### PUBLIC

resource "aws_subnet" "public" {
  for_each                = var.public-subnets
  vpc_id                  = aws_vpc.budgetplanner-vpc.id
  cidr_block              = each.value
  availability_zone       = each.key
  map_public_ip_on_launch = true

  tags = {
    Name = "${local.project_name}-public-subnet-${each.key}"
  }
}

resource "aws_route_table_association" "public" {
  for_each       = aws_subnet.public
  subnet_id      = each.value.id
  route_table_id = aws_route_table.public_rt.id
}

resource "aws_route_table" "public_rt" {
  vpc_id = aws_vpc.budgetplanner-vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.gw.id
  }

  tags = {
    Name = "${local.project_name}-public-rt"
  }
}

resource "aws_nat_gateway" "public" {
  allocation_id = aws_eip.nat.id
  subnet_id     = aws_subnet.public["us-east-1a"].id

  tags = {
    Name = "${local.project_name}-nat-gw"
  }
}

### SECURITY

resource "aws_security_group" "load_balancer" {
  name        = "${local.project_name}-alb-sg"
  description = "Security group for load balancer"
  vpc_id      = aws_vpc.budgetplanner-vpc.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${local.project_name}-alb-sg"
  }
}

resource "aws_security_group" "ecs" {
  name        = "${local.project_name}-ecs-sg"
  description = "Security group for ECS tasks"
  vpc_id      = aws_vpc.budgetplanner-vpc.id

  ingress {
    from_port       = 80
    to_port         = 80
    protocol        = "tcp"
    security_groups = [aws_security_group.load_balancer.id]
  }
}