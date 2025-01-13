package CreditService;

import io.qameta.allure.Step;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class CheckMethods {

    // Далее методы для позитивного и негативного теста
    @Step("Проверить, что статус код {expectedCode} возвращается в ответе")
    public void checkStatusCode(int expectedCode, int actualCode) {
        assertEquals(expectedCode, actualCode);
    }

    @Step("Убедиться в том, что в ответе присутствуют следующие непустые поля: {userFields}")
    public void checkFields(Map<String, Object> data, String... userFields) {
        for (String field : userFields) {
            assertTrue(data.containsKey(field), "Поле " + field + " отсутствует в ответе");
            assertNotNull(field, "Поле " + field + " пустое");
        }
    }

    @Step("Убедиться в том, что в ответе не существует следующих полей: {userFields}")
    public void checkMissingFields(Map<String, Object> data, String... userFields) {
        if (data == null) {
            return;
        }
        for (String field : userFields) {
            assertFalse(data.containsKey(field), "Поле " + field + " присутствует в ответе");
        }
    }

    @Step("Убедиться, что поле {field} содержит текст: {text}")
    public void checkText(String field, String text) {
        assertTrue(field.contains(text), "Поле не содержит ожидаемого текста");
    }
}