package CreditService;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.Map;

public class AuthenticationResponse{
    private final Response response;
        private Map<String, Object> dataAfterAuthentication; // объект, возвращающийся при аутентификации
    private String userToken; // Токен для методов, которые требуют авторизацию (возвращается при успешной аутентификации)

    public AuthenticationResponse(Response response) {
        this.response = response;
    }

    // Метод получения данных пользователя после аутентификации
    public Map<String, Object> getDataAfterAuthentication() {
        if (dataAfterAuthentication == null) {
            dataAfterAuthentication = response.jsonPath().getMap("$");
        }
        return dataAfterAuthentication;
    }

    @Step("Получить актуальный статус код")
    public int getActualStatusCode() {
        return response.getStatusCode();
    }

    // Метод получения токена пользователя после успешной аутентификации
    public String getTokenValue() {
        if (userToken == null) {
            userToken = response.jsonPath().getString("token");
        }
        return userToken;
    }

}