package CreditService;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.Map;

public class DeleteOrderRequest {
    private String deleteOrderRequestEndpoint = "http://localhost:8080/loan-service/deleteOrder";

    @Step("Отправить delete запрос на эндпоинт {this.deleteOrderRequestEndpoint} со следующем токеном в заголовке")
    public Response sendDeleteOrderRequest(Map<String, Object> requestBody, String token) {
        return RestAssured.given().headers("Content-Type", "application/json", "Authorization", "Bearer " +token).body(requestBody).delete(deleteOrderRequestEndpoint);
    }
}