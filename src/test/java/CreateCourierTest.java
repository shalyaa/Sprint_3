import client.CourierClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.Courier;
import model.CourierCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static client.CourierClient.createCourier;
import static client.CourierClient.loginCourier;
import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreateCourierTest {

    Response responseCreate;
    Courier courier;
    CourierClient courierClient;
    CourierCredentials courierCredentials;
    int courierId;

    public final static String NOT_ENOUGH_DATA = "Недостаточно данных для создания учетной записи";
    public final static String SAME_LOGIN = "Этот логин уже используется. Попробуйте другой.";

    @Before
    public void data() {
        courier = courier.getRandomCourier();
    }

    @After
    public void deleteCourier() {
        if (!(courier.getLogin().isEmpty()) && !(courier.getPassword().isEmpty())){
            courierCredentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
            courierId = loginCourier(courierCredentials).body().jsonPath().getInt("id");
            courierClient.deleteCourier(courierId);
        }

    }

    @Test
    @DisplayName("Успешное создание курьера")
    public void createCourierSuccessTest() {
        responseCreate = createCourier(courier);

        assertEquals(SC_CREATED, responseCreate.statusCode());
        assertTrue(responseCreate.body().jsonPath().getBoolean("ok"));
    }

    @Test
    @DisplayName("Создание двух одинаковых курьеров")
    public void createIdenticalCouriersTest() {
        responseCreate = createCourier(courier);

        assertEquals("Ожидался код ответа 201", SC_CREATED, responseCreate.statusCode());
        assertTrue("Ожидался ответ true", responseCreate.body().jsonPath().getBoolean("ok"));

        responseCreate = createCourier(courier);

        assertEquals("Ожидался код ответа 409", SC_CONFLICT, responseCreate.statusCode());
        assertEquals("Тело сообщения не соответствует ожидаемому", SAME_LOGIN, responseCreate.body().path("message"));
    }

    @Test
    @DisplayName("Создание двух курьеров с одинаковыми логинами")
    public void createCourierWithSameLoginTest() {
        responseCreate = createCourier(courier);

        assertEquals("Ожидался код ответа 201", SC_CREATED, responseCreate.statusCode());
        assertTrue("Ожидался ответ true", responseCreate.body().jsonPath().getBoolean("ok"));

        Courier courierWithSameLogin = Courier.getRandomCourier();
        courierWithSameLogin.setLogin(courier.getLogin());

        responseCreate = createCourier(courierWithSameLogin);

        assertEquals("Ожидался код ответа 409", SC_CONFLICT, responseCreate.statusCode());
        assertEquals("Тело сообщения не соответствует ожидаемому", SAME_LOGIN, responseCreate.body().path("message"));
    }

    @Test
    @DisplayName("Создание курьера без логина")
    public void createCourierWithoutLoginTest() {
        courier.setLogin("");
        responseCreate = createCourier(courier);

        assertEquals("Ожидался код ответа 400", SC_BAD_REQUEST, responseCreate.statusCode());
        assertEquals("Тело сообщения не соответствует ожидаемому", NOT_ENOUGH_DATA, responseCreate.body().path("message"));
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    public void createCourierWithoutPasswordTest() {
        courier.setPassword("");
        responseCreate = createCourier(courier);

        assertEquals("Ожидался код ответа 400", SC_BAD_REQUEST, responseCreate.statusCode());
        assertEquals("Тело сообщения не соответствует ожидаемому", NOT_ENOUGH_DATA, responseCreate.body().path("message"));
    }
}
