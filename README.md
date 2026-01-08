[JAVA_BADGE]:https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white
[SPRING_BADGE]: https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white
[POSTGRES_BADGE]:https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white
[FLYWAY_BADGE]:https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white
[MAVEN_BADGE]:https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white


<h1 align="center" style="font-weight: bold;">Budget Planner</h1>

![java][JAVA_BADGE]
![spring][SPRING_BADGE]
![postgres][POSTGRES_BADGE]
![flyway][FLYWAY_BADGE]
![maven][MAVEN_BADGE]

<details open="open">
<summary>Table of Contents</summary>

- [Getting started](#started)
  - [Prerequisites](#prerequisites)
  - [Cloning](#cloning)
  - [Environment Variables](#environment-variables)
  - [Starting](#starting)
- [API Endpoints](#routes)
  - [Health Check](#get-health)
  - [Categories](#categories)
  - [Transactions](#transactions)
  - [Summary](#summary)
- [Collaborators](#colab)
- [Contribute](#contribute)

</details>

<p align="center">
  <b>A REST API for managing personal finances with categories, transactions, and financial summaries.</b>
</p>

<h2 id="started">Getting started</h2>

Budget Planner is a Spring Boot application that helps you track your income and expenses, categorize transactions, and get financial summaries.

<h3>Prerequisites</h3>

Before running the project, make sure you have the following installed:

- [Java 21](https://openjdk.org/projects/jdk/21/)
- [Maven](https://maven.apache.org/)
- [PostgreSQL](https://www.postgresql.org/)
- [Git](https://git-scm.com/)

<h3>Cloning</h3>

Clone the project repository:

```bash
git clone https://github.com/rogerbertan/budget-planner.git
cd budgetplanner
```

<h3>Environment Variables</h2>

The application uses the following environment variables for database configuration. You can set them in your system or use the default values:

```bash
DB_HOST=localhost
DB_NAME=budget-planner
DB_USER=postgres
DB_PASSWORD=postgres
```

Alternatively, you can modify the `application.properties` file directly:

```properties
spring.datasource.url=jdbc:postgresql://localhost/budget-planner
spring.datasource.username=postgres
spring.datasource.password=postgres
```

<h3>Starting</h3>

Create the PostgreSQL database:

```bash
createdb budget-planner
```

Run the application using Maven:

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`


<h2 id="routes">API Endpoints</h2>

<h3 id="get-health">Health Check</h3>

| route                  | description                 |
|------------------------|-----------------------------|
| <kbd>GET /health</kbd> | Check if the API is running |

**RESPONSE**
```json
{
  "status": "UP"
}
```

<h3 id="categories">Categories</h3>

| route                              | description             |
|------------------------------------|-------------------------|
| <kbd>GET /categories</kbd>         | Retrieve all categories |
| <kbd>POST /categories</kbd>        | Create a new category   |
| <kbd>PUT /categories/{id}</kbd>    | Update a category       |
| <kbd>DELETE /categories/{id}</kbd> | Delete a category       |

**POST /categories - REQUEST**
```json
{
  "name": "Groceries",
  "type": "EXPENSE"
}
```

**POST /categories - RESPONSE**
```json
{
  "id": 1,
  "name": "Groceries",
  "type": "EXPENSE"
}
```

<h3 id="transactions">Transactions</h3>

| route                                | description                                                    |
|--------------------------------------|----------------------------------------------------------------|
| <kbd>GET /transactions</kbd>         | Retrieve all transactions (paginated, sorted by creation date) |
| <kbd>GET /transactions/{id}</kbd>    | Retrieve a specific transaction                                |
| <kbd>POST /transactions</kbd>        | Create a new transaction                                       |
| <kbd>PUT /transactions/{id}</kbd>    | Update a transaction                                           |
| <kbd>DELETE /transactions/{id}</kbd> | Delete a transaction                                           |

**POST /transactions - REQUEST**
```json
{
  "type": "EXPENSE",
  "amount": 150.50,
  "description": "Weekly grocery shopping",
  "categoryId": 1,
  "transactionDate": "2024-01-15"
}
```

**POST /transactions - RESPONSE**
```json
{
  "id": 1,
  "type": "EXPENSE",
  "amount": 150.50,
  "description": "Weekly grocery shopping",
  "categoryId": 1,
  "transactionDate": "2024-01-15",
  "createdAt": "2024-01-15T10:30:00"
}
```

**GET /transactions - RESPONSE (Paginated)**
```json
{
  "content": [
    {
      "id": 1,
      "type": "EXPENSE",
      "amount": 150.50,
      "description": "Weekly grocery shopping",
      "categoryId": 1,
      "transactionDate": "2024-01-15",
      "createdAt": "2024-01-15T10:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1
}
```

<h3 id="summary">Summary</h3>

| route                                                        | description                              |
|--------------------------------------------------------------|------------------------------------------|
| <kbd>GET /summary/balance</kbd>                              | Get overall balance summary              |
| <kbd>GET /summary/monthly?month={month}&year={year}</kbd>    | Get monthly summary for a specific month |
| <kbd>GET /summary/categories?month={month}&year={year}</kbd> | Get summary grouped by categories        |

**GET /summary/balance - RESPONSE**
```json
{
  "totalIncome": 5000.00,
  "totalExpense": 3200.50,
  "balance": 1799.50
}
```

**GET /summary/monthly?month=1&year=2024 - RESPONSE**
```json
{
  "month": 1,
  "year": 2024,
  "totalIncome": 5000.00,
  "totalExpense": 3200.50,
  "balance": 1799.50
}
```

**GET /summary/categories?month=1&year=2024 - RESPONSE**
```json
[
  {
    "categoryId": 1,
    "categoryName": "Groceries",
    "type": "EXPENSE",
    "total": 450.00
  },
  {
    "categoryId": 2,
    "categoryName": "Salary",
    "type": "INCOME",
    "total": 5000.00
  }
]
```

<h2 id="contribute">Contribute</h2>

Contributions are welcome! Here's how you can contribute to this project:

1. `git clone https://github.com/rogerbertan/budget-planner.git`
2. `git checkout -b feature/NAME`
3. Follow commit patterns
4. Open a Pull Request explaining the problem solved or feature made, if exists, append screenshot of visual modifications and wait for the review!

<h3>Documentations that might help</h3>

[How to create a Pull Request](https://www.atlassian.com/br/git/tutorials/making-a-pull-request)

[Commit pattern](https://gist.github.com/joshbuchea/6f47e86d2510bce28f8e7f42ae84c716)
