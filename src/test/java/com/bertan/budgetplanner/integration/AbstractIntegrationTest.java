package com.bertan.budgetplanner.integration;

import com.bertan.budgetplanner.config.TokenConfig;
import com.bertan.budgetplanner.domain.user.Role;
import com.bertan.budgetplanner.domain.user.User;
import com.bertan.budgetplanner.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HeaderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenConfig tokenConfig;

    protected record TestUser(String email, String token) {
    }

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.port = port;

        String email = register();
        promoteToAdmin(email);
        String token = tokenFor(email);

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }

    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE transactions, categories, users RESTART IDENTITY CASCADE");
    }

    protected RequestSpecification asUser(String token) {
        return given()
                .config(RestAssuredConfig.config()
                        .headerConfig(HeaderConfig.headerConfig().overwriteHeadersWithName("Authorization")))
                .header("Authorization", "Bearer " + token);
    }

    protected TestUser registerAndLogin() {
        String email = register();
        String token = tokenFor(email);
        return new TestUser(email, token);
    }

    protected void promoteToAdmin(String email) {
        User user = findUserByEmail(email);
        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }

    protected String tokenFor(String email) {
        return tokenConfig.generateToken(findUserByEmail(email));
    }

    private User findUserByEmail(String email) {
        return (User) userRepository.findUserByEmail(email)
                .orElseThrow();
    }

    protected String register() {
        String email = "test-" + UUID.randomUUID() + "@example.com";

        given()
                .contentType("application/json")
                .log().all()
                .body("""
                        {"name": "Test User", "email": "%s", "password": "%s"}
                        """.formatted(email, PASSWORD))
                .post("/api/v1/auth/register")
                .then()
                .log().ifValidationFails()
                .statusCode(201);

        return email;
    }
}