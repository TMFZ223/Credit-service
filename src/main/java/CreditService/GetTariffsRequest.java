package CreditService;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GetTariffsRequest {
    private String getTariffsEndpoint = "http://localhost:8080/loan-service/getTariffs";

    @Step("Отправить Get запрос на эндпоинт {this.getTariffsEndpoint}")
    public Response sendGetTariffsRequest() {
        return RestAssured.given().get(getTariffsEndpoint);
    }
}