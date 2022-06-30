import client.CourierClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.Courier;
import model.CourierCredentials;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static client.CourierClient.createCourier;
import static client.CourierClient.loginCourier;
import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AuthCourierTest {

    Response responseCreate;
    Response responseLogin;
    Courier courier;
    CourierCredentials courierCredentials;
    CourierClient courierClient;
    int courierId;

    public static final String NOT_ENOUGH_DATA = "Недостаточно данных для входа";
    public static final String INVALID_DATA = "Учетная запись не найдена";

    @Before
    public void data() {
        courier = courier.getRandomCourier();
        responseCreate = createCourier(courier);
        courierCredentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
    }

    @After
    public void deleteCourier() {
        if (!(courier.getLogin().isEmpty()) && !(courier.getPassword().isEmpty())) {
            courierCredentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
            courierId = loginCourier(courierCredentials).body().jsonPath().getInt("id");
            courierClient.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Авторизация существующего курьера, логин и пароль валидны")
    @Description("Логин и пароль валидны")
    public void authCourierTest() {
        responseLogin = loginCourier(courierCredentials);

        assertEquals(SC_OK, responseLogin.statusCode());
        assertNotNull(responseLogin.body().path("id"));
    }

    @Test
    @DisplayName("Авторизация курьера с пустым логином")
    @Description("Пустой логин")
    public void authCourierWithoutLoginTest() {
        courierCredentials.setLogin("");
        responseLogin = loginCourier(courierCredentials);

        assertEquals(SC_BAD_REQUEST, responseLogin.statusCode());
        assertEquals("Тело сообщения не соответствует ожидаемому", NOT_ENOUGH_DATA, responseLogin.body().path("message"));
    }

    @Test
    @DisplayName("Авторизация курьера с пустым паролем")
    public void authCourierWithoutPasswordTest() {
        courierCredentials.setPassword("");
        responseLogin = loginCourier(courierCredentials);

        assertEquals(SC_BAD_REQUEST, responseLogin.statusCode());
        assertEquals("Тело сообщения не соответствует ожидаемому", NOT_ENOUGH_DATA, responseLogin.body().path("message"));
    }

    @Test
    @DisplayName("Авторизация курьера с неверным логином")
    public void authCourierWithInvalidLoginTest() {
        courierCredentials.setLogin(RandomStringUtils.randomAlphabetic(10));
        responseLogin = loginCourier(courierCredentials);

        assertEquals(SC_NOT_FOUND, responseLogin.statusCode());
        assertEquals("Тело сообщения не соответствует ожидаемому", INVALID_DATA, responseLogin.body().path("message"));
    }

    @Test
    @DisplayName("Авторизация курьера с неверным паролем")
    public void authCourierWithInvalidPasswordTest() {
        courierCredentials.setPassword(RandomStringUtils.randomAlphabetic(10));
        responseLogin = loginCourier(courierCredentials);

        assertEquals(SC_NOT_FOUND, responseLogin.statusCode());
        assertEquals("Тело сообщения не соответствует ожидаемому", INVALID_DATA, responseLogin.body().path("message"));
    }

    @Test
    @DisplayName("Авторизация несуществующего курьера, пароль и логин невалидны")
    public void authCourierWithInvalidDataTest() {
        courierCredentials.setLogin(RandomStringUtils.randomAlphabetic(10));
        courierCredentials.setPassword(RandomStringUtils.randomAlphabetic(10));
        responseLogin = loginCourier(courierCredentials);

        assertEquals(SC_NOT_FOUND, responseLogin.statusCode());
        assertEquals("Тело сообщения не соответствует ожидаемому", INVALID_DATA, responseLogin.body().path("message"));
    }
}
