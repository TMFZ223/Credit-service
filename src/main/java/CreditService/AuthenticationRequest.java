package CreditService;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.Map;

public class AuthenticationRequest {
    private String authenticateEndpoint = "http://localhost:8080/auth/authenticate";

    @Step("Отправить post запрос на эндпоинт {this.authenticateEndpoint} со следующим Json телом:")
    public Response sendAuthenticateRequest(Map<String, Object> requestBody) {
        return RestAssured.given().header("Content-Type", "application/json").body(requestBody).post(authenticateEndpoint);
    }
}