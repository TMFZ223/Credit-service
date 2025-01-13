package CreditService;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.Map;

public class OrderStatusRequest {
    private  String orderStatusEndpoint = "http://localhost:8080/loan-service/getStatusOrder";

    @Step("Отправить Get запрос на эндпоинт {this.orderStatusEndpoint} со следующими параметрами {params} и токеном в заголовке {token}")
    public Response sendGetOrderStatusRequest(Map<String, Object> params, String token) {
        return RestAssured.given().header("Authorization", "Bearer " + token).queryParams(params).get(orderStatusEndpoint);
    }
}