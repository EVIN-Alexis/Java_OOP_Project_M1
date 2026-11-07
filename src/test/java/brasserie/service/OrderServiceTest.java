package brasserie.service;

import brasserie.model.Company;
import brasserie.model.Customer;
import brasserie.model.Order;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    @Test
    void shouldGenerateDailyOrders() {
        Company company = new Company("Ma Brasserie");
        CustomerService customerService = new CustomerService();
        OrderService orderService = new OrderService();

        List<Order> orders = orderService.generateDailyOrders(
                company, customerService
        );

        assertNotNull(orders);
        assertTrue(company.getOrders().containsAll(orders));
    }

    @Test
    void shouldCreateOrderAndAddItToCompany() {
        Company company = new Company("Ma Brasserie");
        company.addBottles("Blonde", 120);

        Customer customer = new Customer(
                "Client Test",
                Arrays.asList("Blonde", "IPA"),
                50, 200, 0.80f
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

        OrderService service = new OrderService();

        Order order = service.createOrder(
                company, customer, "Blonde", 100, 0.75f, 5
        );

        assertNotNull(order);
        assertTrue(company.getOrders().contains(order));
        assertEquals(Order.Status.PENDING, order.getStatus());
    }

    @Test
    void shouldAcceptOrderAndUpdateStockAndMoney() {
        Company company = new Company("Ma Brasserie");
        company.addBottles("Blonde", 120);

        Customer customer = new Customer(
                "Client Test",
                Arrays.asList("Blonde", "IPA"),
                50, 200, 0.80f
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

        OrderService service = new OrderService();

        Order order = service.createOrder(
                company, customer, "Blonde", 100, 0.75f, 5
        );

        float moneyBefore = company.getMoney();
        boolean accepted = service.acceptOrder(company, order);

        assertTrue(accepted);
        assertEquals(Order.Status.ACCEPTED, order.getStatus());
        assertEquals(20, company.getBottleStock("Blonde"));
        assertEquals(moneyBefore + 75.0f, company.getMoney(), 0.01f);
    }

    @Test
    void shouldNotAcceptSameOrderTwice() {
        Company company = new Company("Ma Brasserie");
        company.addBottles("Blonde", 120);

        Customer customer = new Customer(
                "Client Test",
                Arrays.asList("Blonde"),
                50, 200, 0.80f
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

        OrderService service = new OrderService();

        Order order = service.createOrder(
                company, customer, "Blonde", 100, 0.75f, 5
        );

        assertTrue(service.acceptOrder(company, order));

        int stockAfterFirstSale = company.getBottleStock("Blonde");

        assertFalse(service.acceptOrder(company, order));
        assertEquals(stockAfterFirstSale, company.getBottleStock("Blonde"));
    }

    @Test
    void shouldRetrieveOrdersFromCompany() {
        Company company = new Company("Ma Brasserie");
        OrderService service = new OrderService();

        Customer customer = new Customer(
                "Client Test",
                Arrays.asList("Blonde"),
                50, 200, 0.80f
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

        Order order = service.createOrder(
                company, customer, "Blonde", 100, 0.75f, 5
        );

        OrderService anotherService = new OrderService();

        assertEquals(1, anotherService.getOrders(company).size());
        assertTrue(anotherService.getOrders(company).contains(order));
    }
}