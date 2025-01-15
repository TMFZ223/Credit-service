package CreditService;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.*;

public class GetTariffResponse {
    private final Response response;
    private Map<String, Object> tariffObject; // Объект тарифов

    public GetTariffResponse(Response response) {
        this.response = response;
    }

    @Step("Получить актуальный статус код")
    public int getActualStatusCode() {
        return response.getStatusCode();
    }

    // Получение данных по тарифам для подачи заявки на кредит
    public List<String> getTariffs() {
        if (tariffObject == null) {
            tariffObject = response.jsonPath().getMap("data");
        }
        List<Map<String, Object>> tariffs = response.jsonPath().getList("data.tariffs");
        // Объединяем ключи из всех тарифов в один набор, чтобы не было дублей
        Set<String> allKeys = new HashSet<>();
        for (Map<String, Object> singleTariff : tariffs) {
            allKeys.addAll(singleTariff.keySet());
        }
        // Преобразовать в список
        return new ArrayList<>(allKeys);
    }

    // Получение длины списка тарифов
    public  int getTariffsObjectSize() {
        List<Map<String, Object>> tariffs = response.jsonPath().getList("data.tariffs");
        return  tariffs.size();
    }
}