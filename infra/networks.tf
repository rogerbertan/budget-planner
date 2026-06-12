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

resource "aws_eip" "nat" {

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

resource "aws_route_table" "private_rt" {
  vpc_id = aws_vpc.budgetplanner-vpc.id

  route {
    cidr_block = "0.0.0.0/0"
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