package com.bertan.budgetplanner.integration;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

class CategoryControllerIT extends AbstractIntegrationTest {

    @Test
    void shouldCreateCategory() {
        given()
                .contentType("application/json")
                .body("""
                        {"name": "Salary", "type": "INCOME"}
                        """)
                .when()
                .post("/categories")
                .then()
                .statusCode(201)
                .body("name", equalTo("Salary"))
                .body("type", equalTo("INCOME"))
                .body("id", org.hamcrest.Matchers.notNullValue());
    }

    @Test
    void shouldListCreatedCategories() {
        given().contentType("application/json").body("""
                {"name": "Salary", "type": "INCOME"}
                """).post("/categories");
        given().contentType("application/json").body("""
                {"name": "Groceries", "type": "EXPENSE"}
                """).post("/categories");

        given()
                .when()
                .get("/categories")
                .then()
                .statusCode(200)
                .body("$", hasSize(2));
    }

    @Test
    void shouldUpdateCategoryWhenItExists() {
        Long id = given().contentType("application/json").body("""
                {"name": "Salary", "type": "INCOME"}
                """).post("/categories").jsonPath().getLong("id");

        given()
                .contentType("application/json")
                .body("""
                        {"name": "Updated Salary", "type": "INCOME"}
                        """)
                .when()
                .put("/categories/{id}", id)
                .then()
                .statusCode(200)
                .body("name", equalTo("Updated Salary"));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentCategory() {
        given()
                .contentType("application/json")
                .body("""
                        {"name": "Updated Salary", "type": "INCOME"}
                        """)
                .when()
                .put("/categories/{id}", 999999L)
                .then()
                .statusCode(404);
    }

    @Test
    void shouldDeleteCategoryWhenItExists() {
        Long id = given().contentType("application/json").body("""
                {"name": "Salary", "type": "INCOME"}
                """).post("/categories").jsonPath().getLong("id");

        given()
                .when()
                .delete("/categories/{id}", id)
                .then()
                .statusCode(204);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentCategory() {
        given()
                .when()
                .delete("/categories/{id}", 999999L)
                .then()
                .statusCode(404);
    }

    @Test
    void shouldReturn400WhenPayloadIsMalformedJson() {
        given()
                .contentType("application/json")
                .body("{ this is not valid json")
                .when()
                .post("/categories")
                .then()
                .statusCode(400);
    }
}
