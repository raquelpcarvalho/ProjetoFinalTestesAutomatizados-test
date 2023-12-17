package br.ada.projetofinaltestesautomatizados.test.user;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class UserDefinitions {

    private RequestSpecification request = RestAssured.given()
            .baseUri("http://localhost:8080")
            .contentType(ContentType.JSON);
    private Response response = null;
    private User user = new User();

    @Given("user is unknown")
    public void userIsUnknown() {
        user = new User();
        user.setName(RandomStringUtils.randomAlphabetic(20));
        user.setCpf(RandomStringUtils.randomAlphabetic(11));
    }

    @When("user is registered with success")
    public void userIsRegistered() {
        response = request.body(user).when().post("/user");
        response.then().statusCode(201);
    }

    @Then("user is known")
    public void userIsKnown() {
        response = request.when().get("/user?name="+user.getName());
        response.then().statusCode(200);
        List<User> found = response.jsonPath().getList("", User.class);
        Assertions.assertFalse(found.isEmpty());
        String name = found.get(0).getName();
        Assertions.assertEquals(user.getName(), name);
    }

    @Given("user without document")
    public void userWithoutDocument() {
        user = new User();
        user.setName(RandomStringUtils.randomAlphabetic(20));
        user.setCpf(null);
    }

    @When("user failed to register")
    public void userFailedToRegister() {
        response = request.body(user).when().post("/user");
        response.then().statusCode(400);
    }

    @Then("notify cpf must be not null")
    public void notifyDocumentMustBeNotNull() {
        String failReason = response.jsonPath().get("errors[0].cpf");
        Assertions.assertEquals("must not be null", failReason);
    }

    @And("user is still unknown")
    public void userIsStillUnknown() {
        response = request.when().get("/user?name="+user.getName());
        response.then().statusCode(200);
        List<User> found = response.jsonPath().getList("$");
        Assertions.assertTrue(found.isEmpty());
    }

    @And("user has an invalid email")
    public void userHasInvalidEmail() {
        user.setEmail("invalid-email");
    }

    @And("user has a short password")
    public void userHasShortPassword() {
        user.setPassword("pass");
    }

    @Then("notify email must be valid")
    public void notifyEmailMustBeValid() {
        String failReason = response.jsonPath().get("errors[0].email");
        Assertions.assertEquals("must be a valid email address", failReason);
    }

    @Then("notify password must be at least 8 characters")
    public void notifyPasswordMustBeAtLeast8Characters() {
        String failReason = response.jsonPath().get("errors[0].password");
        Assertions.assertEquals("length must be at least 8 characters", failReason);
    }



}
