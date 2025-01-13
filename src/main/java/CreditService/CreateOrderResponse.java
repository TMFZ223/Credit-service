package CreditService;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.Map;

public class CreateOrderResponse {
    private  final Response response;
    private Map<String, Object> dataAfterCreateOrder; // Объект после отправки запроса на создание заявки
    private Map<String, Object> error; // Объект ошибки при неудачной отправке запроса для оформления заявки

    public CreateOrderResponse(Response response) {
        this.response = response;
    }

    @Step("Получить актуальный статус код")
    public int getActualStatusCode() {
        return response.getStatusCode();
    }

    // Получение данных после отправки запроса на оформление заявки
    public Map<String, Object> getDataAfterCreateOrder() {
if (dataAfterCreateOrder == null) {
    dataAfterCreateOrder = response.jsonPath().getMap("data");
}
return  dataAfterCreateOrder;
    }

    // Получение идентификатора заявки
    public String getOrderId() {
        String orderId = response.jsonPath().getString("data.orderId");
        return  orderId;
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