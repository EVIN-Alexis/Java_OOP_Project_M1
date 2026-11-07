package brasserie.model;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateOrderCorrectly() {
        Customer customer = new Customer(
                "Le Petit Houblon",
                Arrays.asList("Blonde", "IPA"),
                50,
                200,
                2.50f
        ) {
            @Override
            public float calculateOfferedPrice(float variation) {
                return 0;
            }

            @Override
            public String getCustomerType() {
                return "";
            }
        };

        Order order = new Order(
                customer,
                "Blonde",
                100,
                2.50f,
                5
        );

        assertEquals("Le Petit Houblon", order.getCustomer().getName());
        assertEquals("Blonde", order.getStyle());
        assertEquals(100, order.getQuantity());
        assertEquals(2.50f, order.getPricePerBottle(), 0.001f);
        assertEquals(5, order.getDeadlineDay());
        assertEquals(Order.Status.PENDING, order.getStatus());
    }

    @Test
    void shouldCalculateTotalPriceCorrectly() {
        Customer customer = new Customer(
                "Le Petit Houblon",
                Arrays.asList("Blonde"),
                50,
                200,
                2.50f
        ) {
            @Override
            public float calculateOfferedPrice(float variation) {
                return 0;
            }

            @Override
            public String getCustomerType() {
                return "";
            }
        };

        Order order = new Order(
                customer,
                "Blonde",
                100,
                2.50f,
                5
        );

        assertEquals(250.00f, order.getTotalPrice(), 0.001f);
    }

    @Test
    void shouldDetectExpiredOrder() {
        Customer customer = new Customer(
                "Le Petit Houblon",
                Arrays.asList("Blonde"),
                50,
                200,
                2.50f
        ) {
            @Override
            public float calculateOfferedPrice(float variation) {
                return 0;
            }

            @Override
            public String getCustomerType() {
                return "";
            }
        };

        Order order = new Order(
                customer,
                "Blonde",
                100,
                2.50f,
                5
        );

        assertTrue(order.isExpired(6));
    }

    @Test
    void shouldNotExpireOrderBeforeDeadline() {
        Customer customer = new Customer(
                "Le Petit Houblon",
                Arrays.asList("Blonde"),
                50,
                200,
                2.50f
        ) {
            @Override
            public float calculateOfferedPrice(float variation) {
                return 0;
            }

            @Override
            public String getCustomerType() {
                return "";
            }
        };

        Order order = new Order(
                customer,
                "Blonde",
                100,
                2.50f,
                5
        );

        assertFalse(order.isExpired(4));
    }
}