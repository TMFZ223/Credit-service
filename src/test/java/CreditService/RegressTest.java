package CreditService;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.HashMap;
import java.util.Map;

@Epic("Регрессионный набор")
public class RegressTest {
    private AuthenticationRequest authenticationRequest;
    private CheckMethods checkMethods;
    private Map<String, Object> authenticateBody = new HashMap<>(); // Тело запроса для аутентификации
    private Map<String, Object> orderBody = new HashMap<>(); // Тело запроса для подачи заявки
    private Map<String, Object> orderStatusParams = new HashMap<>(); // Query параметры для получения статуса заявки
    private Map<String, Object> deleteOrderBody = new HashMap<>(); // Тело для запроса удаления заявки
    private int expectedStatusCode; // ожидаемый статус код

    public RegressTest() {
        this.authenticationRequest = new AuthenticationRequest();
        this.checkMethods = new CheckMethods();
    }

    @ParameterizedTest
    @ValueSource(strings = {"email", "password"})
    @Description("Тест аутентификации с пропуском одного из полей")
    @DisplayName("Тест аутентификации с пропуском одного из полей")
    public void negativeAuthenticationTest(String field) {
        authenticateBody.put("email", "ivanov@mail.ru");
        authenticateBody.put("password", "1234");
        authenticateBody.remove(field);
        Response loginResponse = authenticationRequest.sendAuthenticateRequest(authenticateBody);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(loginResponse);
        expectedStatusCode = 403;
        checkMethods.checkStatusCode(expectedStatusCode, authenticationResponse.getActualStatusCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ivanovmail.ru", "ivanov@mailru", "ivanovmailru"})
    @Description("Обработка некорректных значений электронной почты при аутентификации")
    @DisplayName("Обработка некорректных значений электронной почты при аутентификации")
    public  void  wrongEmeilValuesTest(String wrongEmail) {
        authenticateBody.put("email", wrongEmail);
                authenticateBody.put("password", "1234");
        Response loginResponse = authenticationRequest.sendAuthenticateRequest(authenticateBody);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(loginResponse);
        expectedStatusCode = 403;
        checkMethods.checkStatusCode(expectedStatusCode, authenticationResponse.getActualStatusCode());
    }

    @ParameterizedTest
    @CsvSource({"2, 1, 'Пользователь не найден'", "1, 4, 'Тариф не найден'"})
    @Description("Параметризированный тест оформления заявки для несуществующего пользователя и существующего тарифа, затем для существующего пользователя и не существующего тарифа")
    @DisplayName("Параметризированный тест оформления заявки для несуществующего пользователя и существующего тарифа, затем для существующего пользователя и не существующего тарифа")
    public void testCreateOrderForInvalidUserAndTariff(Integer userId, Integer tariffId, String errorMessage) {
        authenticateBody.put("email", "ivanov@mail.ru");
        authenticateBody.put("password", "1234");
        orderBody.put("userId", userId);
        orderBody.put("tariffId", tariffId);
        Response loginResponse = authenticationRequest.sendAuthenticateRequest(authenticateBody); // Запрос на аутентификацию
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(loginResponse);
        String generatedToken = authenticationResponse.getTokenValue();
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        Response createOrder = createOrderRequest.sendCreateOrderRequest(orderBody, generatedToken);
        CreateOrderResponse createOrderResponse = new CreateOrderResponse(createOrder);
        expectedStatusCode = 400;
        checkMethods.checkStatusCode(expectedStatusCode, createOrderResponse.getActualStatusCode());
        checkMethods.checkText(errorMessage, createOrderResponse.getErrorMessageText());
    }

    @ParameterizedTest
    @CsvSource({"'unknown_id', 'Заявка не найдена'"})
    @Description("Просмотр несуществующей заявки")
    @DisplayName("Просмотр несуществующей заявки")
    public void testInvalidOrderWatch(String orderId, String errorMessage) {
        authenticateBody.put("email", "ivanov@mail.ru");
        authenticateBody.put("password", "1234");
        Response loginResponse = authenticationRequest.sendAuthenticateRequest(authenticateBody);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(loginResponse);
        String generatedToken = authenticationResponse.getTokenValue();
        orderStatusParams.put("orderId", orderId);
        OrderStatusRequest orderStatusRequest = new OrderStatusRequest();
        Response getStatus = orderStatusRequest.sendGetOrderStatusRequest(orderStatusParams, generatedToken);
        OrderStatusResponse orderStatusResponse = new OrderStatusResponse(getStatus);
        expectedStatusCode = 400;
        checkMethods.checkStatusCode(expectedStatusCode, orderStatusResponse.getActualStatusCode());
        checkMethods.checkText( errorMessage, orderStatusResponse.getErrorMessageText());
    }

    @ParameterizedTest
    @ValueSource(strings = {"aYZFErSQHWWnBOVUfVFu"})
    @Description("Тест удаления несуществующей заявки")
    @DisplayName("Тест удаления несуществующей заявки")
    public void testDeleteInvalidOrder(String orderId) {
        authenticateBody.put("email", "ivanov@mail.ru");
        authenticateBody.put("password", "1234");
        Response loginResponse = authenticationRequest.sendAuthenticateRequest(authenticateBody); // Запрос на аутентификацию
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(loginResponse);
        String generatedToken = authenticationResponse.getTokenValue();
        deleteOrderBody.put("userId", 1);
        deleteOrderBody.put("orderId", orderId);
        DeleteOrderRequest deleteOrderRequest = new DeleteOrderRequest();
        Response deleteOrder = deleteOrderRequest.sendDeleteOrderRequest(deleteOrderBody, generatedToken);
        DeleteOrderResponse deleteOrderResponse = new DeleteOrderResponse(deleteOrder);
        expectedStatusCode = 400;
                checkMethods.checkStatusCode(expectedStatusCode, deleteOrderResponse.getActualStatusCode());
                checkMethods.checkText("Заявка не найдена", deleteOrderResponse.getErrorMessageText());
    }
    }