package client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends BaseApi {

    @Step("Создание заказа")
    public static Response createOrder(Order order) {
        return given()
                .spec(getRecSpec())
                .body(order)
                .when()
                .post(BASE_URL + "/api/v1/orders");

    }

    @Step("Получение списка заказов")
    public static Response getOrderList() {
        return given()
                .spec(getRecSpec())
                .when()
                .get(BASE_URL + "/api/v1/orders");
    }
}
