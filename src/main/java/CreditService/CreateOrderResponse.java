package CreditService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateOrderResponse {
    private final Response response;
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
    public List<String> getDataAfterCreateOrder() {
        if (dataAfterCreateOrder == null) {
            dataAfterCreateOrder = response.jsonPath().getMap("data");
        }
        List<String> fields = new ArrayList<>(dataAfterCreateOrder.keySet());
        return fields;
    }

    // Получение идентификатора заявки
    public String getOrderId() {
        String orderId = response.jsonPath().getString("data.orderId");
        return orderId;
    }

    @Step("Записать идентификатор заявки в файл для последующего использования в тесте")
    public void writeOrderIdToFile() {
        String orderIdValue = getOrderId();
        File file = new File("orderId.txt");

        // Определяем, сколько строк в текущем файле
        long lineCount = 0;
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                lineCount = reader.lines().count();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Если в файле уже 3 или более строк — перезаписываем
        // Иначе — дописываем в конец
        boolean appendMode = lineCount < 3;

        // автоматизация с помощью try-with-resources закрытия ресурсов
        try (FileWriter writer = new FileWriter(file, appendMode)) {
            // Запись идентификатора в файл
            writer.write("orderId: " + orderIdValue + System.lineSeparator());
        } catch (IOException e) {
            // Обработка ошибки записи
            e.printStackTrace();
        }
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