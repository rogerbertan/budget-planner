package com.bertan.budgetplanner.integration;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class AuthControllerIT extends AbstractIntegrationTest {

    private String uniqueEmail() {
        return "auth-" + UUID.randomUUID() + "@example.com";
    }

    @Test
    void shouldRegisterUser() {
        String email = uniqueEmail();

        given()
                .contentType("application/json")
                .body("""
                        {"name": "Roger Bertan", "email": "%s", "password": "P@ssw0rd123"}
                        """.formatted(email))
                .when()
                .post("/api/v1/auth/register")
                .then()
                .statusCode(201)
                .body("name", equalTo("Roger Bertan"))
                .body("email", equalTo(email));
    }

    @Test
    void shouldReturn400WhenRegisteringWithBlankFields() {
        given()
                .contentType("application/json")
                .body("""
                        {"name": "", "email": "", "password": ""}
                        """)
                .when()
                .post("/api/v1/auth/register")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldLoginAndReturnTokenWhenCredentialsAreValid() {
        String email = uniqueEmail();
        given()
                .contentType("application/json")
                .body("""
                        {"name": "Roger Bertan", "email": "%s", "password": "P@ssw0rd123"}
                        """.formatted(email))
                .post("/api/v1/auth/register");

        given()
                .contentType("application/json")
                .body("""
                        {"email": "%s", "password": "P@ssw0rd123"}
                        """.formatted(email))
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    void shouldReturn400WhenLoggingInWithBlankFields() {
        given()
                .contentType("application/json")
                .body("""
                        {"email": "", "password": ""}
                        """)
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldRejectLoginWithWrongPassword() {
        String email = uniqueEmail();
        given()
                .contentType("application/json")
                .body("""
                        {"name": "Roger Bertan", "email": "%s", "password": "P@ssw0rd123"}
                        """.formatted(email))
                .post("/api/v1/auth/register");

        given()
                .contentType("application/json")
                .body("""
                        {"email": "%s", "password": "wrong-password"}
                        """.formatted(email))
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(500);
    }

    @Test
    void shouldRejectLoginWithNonExistentEmail() {
        given()
                .contentType("application/json")
                .body("""
                        {"email": "%s", "password": "P@ssw0rd123"}
                        """.formatted(uniqueEmail()))
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(500);
    }
}