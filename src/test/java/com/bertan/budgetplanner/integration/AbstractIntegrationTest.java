package com.bertan.budgetplanner.integration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
public abstract class AbstractIntegrationTest {

    private static final String PASSWORD = "P@ssw0rd123";

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.port = port;
        String token = authenticate();
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }

    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE transactions, categories, users RESTART IDENTITY CASCADE");
    }

    private String authenticate() {
        String email = "test-" + UUID.randomUUID() + "@example.com";

        given()
                .contentType("application/json")
                .body("""
                        {"name": "Test User", "email": "%s", "password": "%s"}
                        """.formatted(email, PASSWORD))
                .post("/api/v1/auth/register");

        return given()
                .contentType("application/json")
                .body("""
                        {"email": "%s", "password": "%s"}
                        """.formatted(email, PASSWORD))
                .post("/api/v1/auth/login")
                .jsonPath()
                .getString("token");
    }
}
