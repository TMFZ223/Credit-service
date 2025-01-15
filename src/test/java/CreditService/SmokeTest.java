package CreditService;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.HashMap;
import java.util.Map;

@Epic("Smoke набор")
class SmokeTest {
    private AuthenticationRequest authenticationRequest;
    private CheckMethods checkMethods;
    private Map<String, Object> authenticateBody = new HashMap<>(); // тело запроса для аутентификации
    private Map<String, Object> orderBody = new HashMap<>(); // Тело запроса для подачи заявки
    private Map<String, Object> orderStatusParams = new HashMap<>(); // Query параметры для получения статуса заявки
    private int expectedStatusCode; // ожидаемый статус код

    public SmokeTest() {
        this.authenticationRequest = new AuthenticationRequest();
        this.checkMethods = new CheckMethods();
    }

    @Test
    @Description("Просмотр тарифов для подачи заявки на кредит")
    public void testGetTariffs() {
        GetTariffsRequest getTariffsRequest = new GetTariffsRequest();
        Response getCreditTariff = getTariffsRequest.sendGetTariffsRequest();
        GetTariffResponse getTariffResponse = new GetTariffResponse(getCreditTariff);
        expectedStatusCode = 200;
        checkMethods.checkStatusCode(expectedStatusCode, getTariffResponse.getActualStatusCode());
        checkMethods.checkFields(getTariffResponse.getTariffs(), "id", "type", "interestRate");
    }

    @Test
    @Description("Позитивный тест аутентификации")
    @DisplayName("Позетивный тест аутентификации")
    public void positiveTestAuthentication() {
        authenticateBody.put("email", "ivanov@mail.ru");
        authenticateBody.put("password", "1234");
        Response loginResponse = authenticationRequest.sendAuthenticateRequest(authenticateBody);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(loginResponse);
        expectedStatusCode = 200;
        checkMethods.checkStatusCode(expectedStatusCode, authenticationResponse.getActualStatusCode());
        checkMethods.checkFields(authenticationResponse.getDataAfterAuthentication(), "token");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @Description("Параметризированный тест оформления заявки для существующего пользователя и существующих тарифов и просмотр статуса оформленных заявок")
    @DisplayName("Параметризированный тест оформления заявки для существующего пользователя и существующих тарифов")
    public void testCreateOrderForDiferents(Integer tariffId) {
        authenticateBody.put("email", "ivanov@mail.ru");
        authenticateBody.put("password", "1234");
        orderBody.put("userId", 1);
        orderBody.put("tariffId", tariffId);
        Response loginResponse = authenticationRequest.sendAuthenticateRequest(authenticateBody); // Запрос на аутентификацию
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(loginResponse);
        String generatedToken = authenticationResponse.getTokenValue();
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        Response createOrder = createOrderRequest.sendCreateOrderRequest(orderBody, generatedToken); // Создание заявки
        CreateOrderResponse createOrderResponse = new CreateOrderResponse(createOrder);
        createOrderResponse.writeOrderIdToFile();
        expectedStatusCode = 200;
        checkMethods.checkStatusCode(expectedStatusCode, createOrderResponse.getActualStatusCode());
        checkMethods.checkFields(createOrderResponse.getDataAfterCreateOrder(), "orderId");
    }

    @ParameterizedTest
    @ValueSource(strings = {"3ca2d511-af3b-4604-bfce-b133f7761829", "d24b534e-a936-4cd5-b25d-a021f4fabccd", "7a0e678a-8ce6-4433-8cc3-c1185ecbf09f"})
    // Идентификаторы из файла. При перезаписи файла нужно заменить текущие значения в тесте. Если тест проходить сразу, то ожидаемый статус заявки в прогрессе, через некоторое время нужно поменять на одобрено
    @Description("Параметризованный тест просмотра статуса заявок. идентификаторы ранее записаны в файл")
    public void testCheckOrderStatus(String orderId) {
        authenticateBody.put("email", "ivanov@mail.ru");
        authenticateBody.put("password", "1234");
        Response loginResponse = authenticationRequest.sendAuthenticateRequest(authenticateBody); // Запрос на аутентификацию
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(loginResponse);
        String generatedToken = authenticationResponse.getTokenValue();
        orderStatusParams.put("orderId", orderId);
        OrderStatusRequest orderStatusRequest = new OrderStatusRequest();
        Response getStatus = orderStatusRequest.sendGetOrderStatusRequest(orderStatusParams, generatedToken);
        OrderStatusResponse orderStatusResponse = new OrderStatusResponse(getStatus);
        expectedStatusCode = 200;
        checkMethods.checkStatusCode(expectedStatusCode, orderStatusResponse.getActualStatusCode());
        checkMethods.checkFields(orderStatusResponse.getDataAfterGetOrderStatus(), "orderStatus");
        checkMethods.checkText(orderStatusResponse.getOrderStatus(), "IN_PROGRESS");
    }
}