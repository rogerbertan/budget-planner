package com.bertan.budgetplanner.integration;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

class TransactionControllerIT extends AbstractIntegrationTest {

    private Long createCategory(String name, String type) {
        return given()
                .contentType("application/json")
                .body("{\"name\": \"%s\", \"type\": \"%s\"}".formatted(name, type))
                .post("/api/v1/categories")
                .jsonPath()
                .getLong("id");
    }

    @Test
    void shouldCreateTransactionWhenCategoryExists() {
        Long categoryId = createCategory("Food", "EXPENSE");

        given()
                .contentType("application/json")
                .body("""
                        {"type": "EXPENSE", "amount": 50.00, "description": "Lunch", "categoryId": %d, "transactionDate": "2026-06-22"}
                        """.formatted(categoryId))
                .when()
                .post("/api/v1/transactions")
                .then()
                .statusCode(201)
                .body("description", equalTo("Lunch"))
                .body("category", equalTo(categoryId.intValue()))
                .body("id", notNullValue());
    }

    @Test
    void shouldReturn404WhenCreatingTransactionWithNonExistentCategory() {
        given()
                .contentType("application/json")
                .body("""
                        {"type": "EXPENSE", "amount": 50.00, "description": "Lunch", "categoryId": 999999, "transactionDate": "2026-06-22"}
                        """)
                .when()
                .post("/api/v1/transactions")
                .then()
                .statusCode(404);
    }

    @Test
    void shouldGetTransactionById() {
        Long categoryId = createCategory("Food", "EXPENSE");
        Long transactionId = given()
                .contentType("application/json")
                .body("""
                        {"type": "EXPENSE", "amount": 50.00, "description": "Lunch", "categoryId": %d, "transactionDate": "2026-06-22"}
                        """.formatted(categoryId))
                .post("/api/v1/transactions")
                .jsonPath()
                .getLong("id");

        given()
                .when()
                .get("/api/v1/transactions/{id}", transactionId)
                .then()
                .statusCode(200)
                .body("description", equalTo("Lunch"));
    }

    @Test
    void shouldReturn404WhenGettingNonExistentTransaction() {
        given()
                .when()
                .get("/api/v1/transactions/{id}", 999999L)
                .then()
                .statusCode(404);
    }

    @Test
    void shouldListTransactionsPaged() {
        Long categoryId = createCategory("Food", "EXPENSE");
        for (int i = 0; i < 3; i++) {
            given()
                    .contentType("application/json")
                    .body("""
                            {"type": "EXPENSE", "amount": 10.00, "description": "Item %d", "categoryId": %d, "transactionDate": "2026-06-22"}
                            """.formatted(i, categoryId))
                    .post("/api/v1/transactions");
        }

        given()
                .when()
                .get("/api/v1/transactions?page=0&size=2")
                .then()
                .statusCode(200)
                .body("content", hasSize(2))
                .body("totalElements", equalTo(3));
    }

    @Test
    void shouldUpdateTransactionWhenItAndCategoryExist() {
        Long categoryId = createCategory("Food", "EXPENSE");
        Long transactionId = given()
                .contentType("application/json")
                .body("""
                        {"type": "EXPENSE", "amount": 50.00, "description": "Lunch", "categoryId": %d, "transactionDate": "2026-06-22"}
                        """.formatted(categoryId))
                .post("/api/v1/transactions")
                .jsonPath()
                .getLong("id");

        given()
                .contentType("application/json")
                .body("""
                        {"type": "EXPENSE", "amount": 75.00, "description": "Dinner", "categoryId": %d, "transactionDate": "2026-06-22"}
                        """.formatted(categoryId))
                .when()
                .put("/api/v1/transactions/{id}", transactionId)
                .then()
                .statusCode(200)
                .body("description", equalTo("Dinner"));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentTransaction() {
        Long categoryId = createCategory("Food", "EXPENSE");

        given()
                .contentType("application/json")
                .body("""
                        {"type": "EXPENSE", "amount": 75.00, "description": "Dinner", "categoryId": %d, "transactionDate": "2026-06-22"}
                        """.formatted(categoryId))
                .when()
                .put("/api/v1/transactions/{id}", 999999L)
                .then()
                .statusCode(404);
    }

    @Test
    void shouldDeleteTransactionWhenItExists() {
        Long categoryId = createCategory("Food", "EXPENSE");
        Long transactionId = given()
                .contentType("application/json")
                .body("""
                        {"type": "EXPENSE", "amount": 50.00, "description": "Lunch", "categoryId": %d, "transactionDate": "2026-06-22"}
                        """.formatted(categoryId))
                .post("/api/v1/transactions")
                .jsonPath()
                .getLong("id");

        given()
                .when()
                .delete("/api/v1/transactions/{id}", transactionId)
                .then()
                .statusCode(204);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentTransaction() {
        given()
                .when()
                .delete("/api/v1/transactions/{id}", 999999L)
                .then()
                .statusCode(404);
    }

    @Test
    void shouldReturn400WhenPayloadIsMalformedJson() {
        given()
                .contentType("application/json")
                .body("{ this is not valid json")
                .when()
                .post("/api/v1/transactions")
                .then()
                .statusCode(400);
    }
}
