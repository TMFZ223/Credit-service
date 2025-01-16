package CreditService;

import java.util.stream.Stream;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Epic("Smoke набор")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Поскольку в данном тестовом наборе важен порядок прохождения тестов, присутствует данная аннотация
public class SmokeTest {
    private AuthenticationRequest authenticationRequest;
    private CheckMethods checkMethods;
    private Map<String, Object> authenticateBody = new HashMap<>(); // тело запроса для аутентификации
    private Map<String, Object> orderBody = new HashMap<>(); // Тело запроса для подачи заявки
    private Map<String, Object> orderStatusParams = new HashMap<>(); // Query параметры для получения статуса заявки
    private Map<String, Object> deleteOrderBody = new HashMap<>(); // Тело для запроса удаления заявки
    private int expectedStatusCode; // ожидаемый статус код
    private static CreditServiceDB crsDb; // Для использования в предпоследнем и последнем тесте

    static {
        crsDb = new CreditServiceDB();
        crsDb.connectToDataBase();
    }

    private static Stream<String> orderIdProvider() {
        List<String> orderIds = crsDb.getOrderIds(); // Получаем список идентификаторов заявок
        return orderIds.stream();
    }

    public SmokeTest() {
        this.authenticationRequest = new AuthenticationRequest();
        this.checkMethods = new CheckMethods();
    }

    @Test
    @Description("Просмотр тарифов для подачи заявки на кредит")
    @DisplayName("Просмотр тарифов для подачи заявки на кредит")
    @Order(1)
    public void GetTariffsTest() {
        GetTariffsRequest getTariffsRequest = new GetTariffsRequest();
        Response getCreditTariff = getTariffsRequest.sendGetTariffsRequest();
        GetTariffResponse getTariffResponse = new GetTariffResponse(getCreditTariff);
        expectedStatusCode = 200;
        checkMethods.checkStatusCode(expectedStatusCode, getTariffResponse.getActualStatusCode());
        checkMethods.checkFields(getTariffResponse.getTariffs(), "id", "type", "interestRate");
        checkMethods.checkLenObjects(3, getTariffResponse.getTariffsObjectSize());
    }

    @Test
    @Description("Позитивный тест аутентификации")
    @DisplayName("Позетивный тест аутентификации")
    @Order(2)
    public void authenticationTestPositive() {
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
    @Description("Параметризированный тест оформления заявки для существующего пользователя и существующих тарифов")
    @DisplayName("Параметризированный тест оформления заявки для существующего пользователя и существующих тарифов")
    @Order(3)
    public void testOrderCreate(Integer tariffId) {
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
        expectedStatusCode = 200;
        checkMethods.checkStatusCode(expectedStatusCode, createOrderResponse.getActualStatusCode());
        checkMethods.checkFields(createOrderResponse.getDataAfterCreateOrder(), "orderId");
    }

    @ParameterizedTest
    @MethodSource("orderIdProvider")
    @Description("Параметризованный тест просмотра статуса недавно оформленных заявок")
    @DisplayName("Параметризованный тест просмотра статуса недавно оформленных заявок")
    // Данные подтягиваются из таблицы базы данных
    @Order(4)
    public void testOrderStatusGet(String orderId) {
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
        checkMethods.checkText("IN_PROGRESS", orderStatusResponse.getOrderStatus());
    }

    @ParameterizedTest
    @MethodSource("orderIdProvider")
    @Description("Параметризованный тест удаления оформленных заявок")
    @DisplayName("Параметризованный тест удаления оформленных заявок")
    @Order(5)
    public void testDeleteOrder(String orderId) {
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
        expectedStatusCode = 200;
        checkMethods.checkStatusCode(expectedStatusCode, deleteOrderResponse.getActualStatusCode());
    }
}