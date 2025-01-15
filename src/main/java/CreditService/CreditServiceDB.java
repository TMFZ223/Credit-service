package CreditService;

import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CreditServiceDB {
    private static final Logger log = LoggerFactory.getLogger(CreditServiceDB.class);
    private Connection conn; // Поле для хранения соединения

    @Step("Установить соединение с базой данных")
    public void connectToDataBase() {
        String url = "jdbc:postgresql://localhost:5432/mts-credit"; // Адрес базы данных
        String user = "user";      // имя пользователя
        String password = "1234";  // пароль

        try {
            // Устанавливаем соединение
            conn = DriverManager.getConnection(url, user, password);
            log.info("Соединение с БД успешно установлено.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Step("Получить необходимые записи из таблицы")
    public List<String> getOrderIds() {
        List<String> orderIds = new ArrayList<>();
        String query = "SELECT order_id FROM loan_order";
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {
            while (rs.next()) {
                orderIds.add(rs.getString("order_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderIds;
    }

    @Step("Закрыть соединение с БД")
    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                log.info("Соединение с БД закрыто.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}