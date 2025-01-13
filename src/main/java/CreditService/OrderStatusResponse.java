package CreditService;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

public class OrderStatusResponse {
    private  final Response response;
    private Map<String, Object> dataAfterGetOrderStatus; // Объект после удачной отправки запроса на получение статуса заявки
    private Map<String, Object> error; // Объект ошибки при неудачной отправке запроса для получения статуса заявки

    public OrderStatusResponse(Response response) {
                this.response = response;
}

@Step("Получить актуальный статус код")
public int getActualStatusCode() {
    return response.getStatusCode();
}

// Получение данных после отправки запроса на просмотр статуса заявки
public Map<String, Object> getDataAfterGetOrderStatus() {
    if (dataAfterGetOrderStatus == null) {
        dataAfterGetOrderStatus = response.jsonPath().getMap("data");
    }
    return dataAfterGetOrderStatus;
}

// Получение статуса заявки
public String getOrderStatus() {
    String orderStatus = response.jsonPath().getString("data.orderStatus");
    return orderStatus;
}

// Получение текста ошибки
public  String getErrorMessageText() {
    String errorMessageText;
    if (error == null) {
        error = response.jsonPath().getMap("error");
    }
    errorMessageText = response.jsonPath().getString("error.message");
    return  errorMessageText;
}
}
