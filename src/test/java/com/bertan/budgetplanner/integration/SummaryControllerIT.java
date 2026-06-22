package com.bertan.budgetplanner.integration;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class SummaryControllerIT extends AbstractIntegrationTest {

    private Long createCategory(String name, String type) {
        return given()
                .contentType("application/json")
                .body("{\"name\": \"%s\", \"type\": \"%s\"}".formatted(name, type))
                .post("/categories")
                .jsonPath()
                .getLong("id");
    }

    private void createTransaction(String type, String amount, Long categoryId, String date) {
        given()
                .contentType("application/json")
                .body("""
                        {"type": "%s", "amount": %s, "description": "Item", "categoryId": %d, "transactionDate": "%s"}
                        """.formatted(type, amount, categoryId, date))
                .post("/transactions");
    }

    @Test
    void shouldReturnBalanceSummary() {
        Long incomeCategory = createCategory("Salary", "INCOME");
        Long expenseCategory = createCategory("Food", "EXPENSE");
        createTransaction("INCOME", "1000.00", incomeCategory, "2026-06-22");
        createTransaction("EXPENSE", "300.00", expenseCategory, "2026-06-22");

        given()
                .when()
                .get("/summary/balance")
                .then()
                .statusCode(200)
                .body("balance", equalTo(700.00f));
    }

    @Test
    void shouldReturnMonthlySummary() {
        Long incomeCategory = createCategory("Salary", "INCOME");
        Long expenseCategory = createCategory("Food", "EXPENSE");
        createTransaction("INCOME", "1000.00", incomeCategory, "2026-06-22");
        createTransaction("EXPENSE", "300.00", expenseCategory, "2026-06-22");

        given()
                .when()
                .get("/summary/monthly?month=6&year=2026")
                .then()
                .statusCode(200)
                .body("totalIncome", equalTo(1000.00f))
                .body("totalExpense", equalTo(300.00f));
    }

    @Test
    void shouldReturnCategoriesSummary() {
        Long expenseCategory = createCategory("Food", "EXPENSE");
        createTransaction("EXPENSE", "150.00", expenseCategory, "2026-06-22");

        given()
                .when()
                .get("/summary/categories?month=6&year=2026")
                .then()
                .statusCode(200)
                .body("[0].category", equalTo("Food"));
    }

    @Test
    void shouldReturn400WhenMonthIsNotNumeric() {
        given()
                .when()
                .get("/summary/monthly?month=abc&year=2026")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldReturn400WhenRequiredParameterIsMissing() {
        given()
                .when()
                .get("/summary/monthly?year=2026")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldReturnOkOnHealthCheck() {
        given()
                .when()
                .get("/health")
                .then()
                .statusCode(200)
                .body(equalTo("OK"));
    }
}
