<!-- Improved compatibility of back to top link -->
<a id="readme-top"></a>

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <h3 align="center">Budget Planner</h3>

  <p align="center">
    A REST API for managing personal finances with categories, transactions, and financial summaries.
    <br />
    <a href="https://github.com/rogerbertan/budget-planner/issues/new?labels=bug">Report Bug</a>
    ·
    <a href="https://github.com/rogerbertan/budget-planner/issues/new?labels=enhancement">Request Feature</a>
  </p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contributing">Contributing</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->
## About The Project

Budget Planner is a Spring Boot application that helps you track your income and expenses, categorize transactions, and get financial summaries. It provides a comprehensive REST API for managing personal finances with support for:

* Transaction tracking with categories
* Income and expense management
* Financial summaries and balance calculations
* Monthly reports and category-based analysis

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Built With

* [![Java][Java-badge]][Java-url]
* [![Spring][Spring-badge]][Spring-url]
* [![PostgreSQL][PostgreSQL-badge]][PostgreSQL-url]
* [![Flyway][Flyway-badge]][Flyway-url]
* [![Maven][Maven-badge]][Maven-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->
## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites

Before running the project, make sure you have the following installed:

* [Java 21](https://openjdk.org/projects/jdk/21/)
* [Maven](https://maven.apache.org/)
* [PostgreSQL](https://www.postgresql.org/)
* [Git](https://git-scm.com/)

### Installation

1. Clone the repository
   ```bash
   git clone https://github.com/rogerbertan/budget-planner.git
   cd budgetplanner
   ```

2. Set up environment variables (optional - defaults are provided)
   ```bash
   export DB_HOST=localhost
   export DB_NAME=budget-planner
   export DB_USER=postgres
   export DB_PASSWORD=postgres
   ```

   Alternatively, modify `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost/budget-planner
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   ```

3. Create the PostgreSQL database
   ```bash
   createdb budget-planner
   ```

4. Run the application using Maven
   ```bash
   ./mvnw spring-boot:run
   ```

The API will be available at `http://localhost:8080`

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- USAGE EXAMPLES -->
## Usage

### Health Check

| route                  | description                 |
|------------------------|-----------------------------|
| <kbd>GET /health</kbd> | Check if the API is running |

**RESPONSE**
```json
{
  "status": "UP"
}
```

### Categories

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

### Transactions

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

### Summary

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

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
[contributors-shield]: https://img.shields.io/github/contributors/rogerbertan/budget-planner.svg?style=for-the-badge
[contributors-url]: https://github.com/rogerbertan/budget-planner/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/rogerbertan/budget-planner.svg?style=for-the-badge
[forks-url]: https://github.com/rogerbertan/budget-planner/network/members
[stars-shield]: https://img.shields.io/github/stars/rogerbertan/budget-planner.svg?style=for-the-badge
[stars-url]: https://github.com/rogerbertan/budget-planner/stargazers
[issues-shield]: https://img.shields.io/github/issues/rogerbertan/budget-planner.svg?style=for-the-badge
[issues-url]: https://github.com/rogerbertan/budget-planner/issues
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/rogerbertan
[Java-badge]: https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white
[Java-url]: https://openjdk.org/
[Spring-badge]: https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white
[Spring-url]: https://spring.io/
[PostgreSQL-badge]: https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white
[PostgreSQL-url]: https://www.postgresql.org/
[Flyway-badge]: https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white
[Flyway-url]: https://flywaydb.org/
[Maven-badge]: https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white
[Maven-url]: https://maven.apache.org/