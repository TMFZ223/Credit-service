package CreditService;
import io.qameta.allure.Step;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CheckMethods {

    // Далее методы для позитивного и негативного теста
    @Step("Проверить, что статус код {expectedCode} возвращается в ответе")
    public void checkStatusCode(int expectedCode, int actualCode) {
        assertEquals(expectedCode, actualCode);
    }

    @Step("Убедиться в том, что в ответе присутствуют следующие поля: {userFields}")
    public void checkFields(List<String> data, String... userFields) {
            for (String field : userFields) {
                assertTrue(data.contains(field), "Поле " + field + " отсутствует в ответе");
                    }
    }

    @Step("Убедиться в том, что в ответе не существует следующих полей: {userFields}")
    public void checkMissingFields(List<String> data, String... userFields) {
        if (data == null) {
            return;
        }
        for (String field : userFields) {
            assertFalse(data.contains(field), "Поле " + field + " присутствует в ответе");
        }
    }

    @Step("Убедиться, что поле {field} содержит текст: {text}")
    public void checkText(String field, String text) {
        assertTrue(field.contains(text), "Поле не содержит ожидаемого текста");
    }
}