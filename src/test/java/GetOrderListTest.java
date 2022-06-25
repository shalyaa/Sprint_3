import io.restassured.response.Response;
import org.junit.Test;

import static client.OrderClient.getOrderList;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GetOrderListTest {

    @Test
    public void getOrderListTest() {
        Response responseOrders = getOrderList();

        assertEquals(SC_OK, responseOrders.statusCode());
        assertNotNull(responseOrders.body().jsonPath().getList("orders"));
    }
}
