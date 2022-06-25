package client;

import io.restassured.response.Response;
import model.Courier;
import model.CourierCredentials;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;

public class CourierClient extends BaseApi {

    public static Response createCourier(Courier courier) {
        return given()
                .spec(BaseApi.getRecSpec())
                .body(courier)
                .when()
                .post(BaseApi.BASE_URL + "/api/v1/courier/");
    }

    public static Response loginCourier(CourierCredentials courierCredentials) {
        return given()
                .spec(getRecSpec())
                .body(courierCredentials)
                .when()
                .post(BASE_URL + "/api/v1/courier/login");

    }

    public static Boolean deleteCourier(int courierId) {
        return given()
                .spec(getRecSpec())
                .when()
                .delete(BASE_URL + "/api/v1/courier/" + courierId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("ok");
    }
}
