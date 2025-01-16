package CreditService;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.Map;

public class CreateOrderRequest {
    private  String createOrderRequestEndpoint = "http://localhost:8080/loan-service/order";

    @Step("Отправить Post запрос на эндпоинт {this.createOrderRequestEndpoint} со следующим телом в формате JSON и токеном в заголовке {token}")
    public Response sendCreateOrderRequest(Map<String, Object> requestBody, String token) {
        return RestAssured.given().headers("Content-Type", "application/json", "Authorization", "Bearer " + token).body(requestBody).post(createOrderRequestEndpoint);
    }
}