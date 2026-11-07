package brasserie.service;

import brasserie.model.Company;
import brasserie.model.InsufficientStockException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SalesServiceTest {

    @Test
    void shouldSellBottlesWhenStockIsSufficient() {
        Company company = new Company("Ma Brasserie");
        SalesService sales = new SalesService();

        company.addBottles("Blonde", 120);

        boolean result = sales.sellBottles(
                company, "Blonde", 10, 2.50f
        );

        assertTrue(result);
        assertEquals(110, company.getBottleStock("Blonde"));
    }

    @Test
    void shouldThrowExceptionWhenStockIsInsufficient() {
        Company company = new Company("Ma Brasserie");
        SalesService sales = new SalesService();

        company.addBottles("Blonde", 10);

        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> sales.sellBottles(
                        company, "Blonde", 100, 2.50f
                )
        );

        assertTrue(exception.getMessage().contains("Blonde"));
        assertEquals(10, company.getBottleStock("Blonde"));
    }
}