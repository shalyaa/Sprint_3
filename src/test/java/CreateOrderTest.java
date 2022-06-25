import io.restassured.response.Response;
import model.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static client.OrderClient.createOrder;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private final String[] colors;

    public CreateOrderTest(String[] colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters
    public static Object[][] getColor() {
        return new Object[][]{
                {new String[]{"BLACK"}},
                {new String[]{"GREY"}},
                {new String[]{"BLACK", "GREY"}},
                {new String[]{}}
        };
    }

    @Test
    public void checkColorInCreateOrderTest() {
        Order order = Order.getRandomOrder();
        order.setColor(colors);

        Response responseOrder = createOrder(order);

        assertEquals(SC_CREATED, responseOrder.statusCode());
        assertNotNull(responseOrder.body().path("track"));
    }
}
