package brasserie.service;

import brasserie.model.Company;
import brasserie.model.Customer;
import brasserie.model.Order;
import brasserie.model.ProfessionalCustomer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class SaveServiceTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void shouldSaveAndLoadCompany() {
        Company company = new Company("Ma Brasserie");

        Customer customer = new ProfessionalCustomer(
                "Client Test",
                Arrays.asList("Blonde", "IPA"),
                50, 200, 0.80f
        );

        Order order = new Order(
                customer, "Blonde", 100, 0.75f, 5
        );

        company.getOrders().add(order);

        SaveService saveService = new SaveService();
        FinanceService financeService = new FinanceService();

        String filePath = temporaryDirectory
                .resolve("test-save.json")
                .toString();

        boolean saved = saveService.saveCompany(
                company, financeService, filePath
        );

        assertTrue(saved);

        Company loadedCompany = saveService.loadCompany(filePath);

        assertNotNull(loadedCompany);
        assertEquals(company.getName(), loadedCompany.getName());
        assertEquals(company.getCurrentDay(), loadedCompany.getCurrentDay());
        assertEquals(company.getMoney(), loadedCompany.getMoney(), 0.01f);

        assertEquals(1, loadedCompany.getOrders().size());

        Order loadedOrder = loadedCompany.getOrders().get(0);

        assertEquals("Client Test", loadedOrder.getCustomer().getName());
        assertInstanceOf(ProfessionalCustomer.class, loadedOrder.getCustomer());
        assertEquals("Blonde", loadedOrder.getStyle());
        assertEquals(100, loadedOrder.getQuantity());
        assertEquals(0.75f, loadedOrder.getPricePerBottle(), 0.001f);
        assertEquals(5, loadedOrder.getDeadlineDay());
        assertEquals(Order.Status.PENDING, loadedOrder.getStatus());
    }
}