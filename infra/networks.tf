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

### PRIVATE

resource "aws_subnet" "private" {
  for_each   = var.private-subnets
  vpc_id     = aws_vpc.budgetplanner-vpc.id
  cidr_block = each.value

  tags = {
    Name = "${local.project_name}-private-subnet-${each.key}"
  }
}

### PUBLIC

resource "aws_subnet" "public" {
  for_each                = var.public-subnets
  vpc_id                  = aws_vpc.budgetplanner-vpc.id
  cidr_block              = each.value
  map_public_ip_on_launch = true

  tags = {
    Name = "${local.project_name}-public-subnet-${each.key}"
  }
}

resource "aws_route_table_association" "public" {
  for_each       = aws_subnet.public
  route_table_id = aws_route_table.public_rt.id
  subnet_id      = each.value.id
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