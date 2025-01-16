package CreditService;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

public class DeleteOrderResponse {
    private final Response response;
    private Map<String, Object> error; // Объект ошибки при неудачной отправке запроса для получения статуса заявки

    public DeleteOrderResponse(Response response) {
        this.response = response;
    }

    @Step("Получить актуальный статус код")
    public int getActualStatusCode() {
                return response.getStatusCode();
    }

    // Получение текста ошибки
    public String getErrorMessageText() {
        if (error == null) {
            error = response.jsonPath().getMap("error");
        }
        String errorMessageText = response.jsonPath().getString("error.message");
        return errorMessageText;
    }
}