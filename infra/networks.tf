resource "aws_vpc" "budgetplanner-vpc" {
  cidr_block       = var.vpc_cidr_block
  instance_tenancy = "default"

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
  vpc_id     = aws_vpc.budgetplanner-vpc.id
  cidr_block = var.cidr_block_private

  tags = {
    Name = "${local.project_name}-private-subnet"
  }
}

### PUBLIC

resource "aws_subnet" "public" {
  vpc_id     = aws_vpc.budgetplanner-vpc.id
  cidr_block = var.cidr_block_public
  map_public_ip_on_launch = true

  tags = {
    Name = "${local.project_name}-public-subnet"
  }
}

resource "aws_route_table_association" "public" {
  route_table_id = aws_route_table.public_rt.id
  subnet_id      = aws_subnet.public.id
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