package brasserie.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerPolymorphismTest {

    @Test
    void regularCustomerKeepsTargetPriceBeforeVariation() {
        Customer customer = new RegularCustomer(
                "Client régulier", List.of("Blonde"), 10, 30, 2.00f
        );

        assertEquals(2.10f, customer.calculateOfferedPrice(0.10f), 0.001f);
        assertEquals("REGULAR", customer.getCustomerType());
    }

    @Test
    void professionalCustomerGetsTenPercentDiscount() {
        Customer customer = new ProfessionalCustomer(
                "Bar professionnel", List.of("IPA"), 20, 80, 2.00f
        );

        assertEquals(1.90f, customer.calculateOfferedPrice(0.10f), 0.001f);
        assertEquals("PROFESSIONAL", customer.getCustomerType());
    }

    @Test
    void sameCustomerReferenceUsesTheConcreteImplementation() {
        List<Customer> customers = List.of(
                new RegularCustomer("Particulier", List.of("Blonde"), 1, 5, 2.00f),
                new ProfessionalCustomer("Restaurant", List.of("Blonde"), 10, 50, 2.00f)
        );

        assertEquals(2.00f, customers.get(0).calculateOfferedPrice(0), 0.001f);
        assertEquals(1.80f, customers.get(1).calculateOfferedPrice(0), 0.001f);
    }

    @Test
    void professionalCustomerShouldImplementDiscountable() {
        ProfessionalCustomer customer = new ProfessionalCustomer(
                "Client professionnel",
                List.of("Blonde"),
                20, 100, 2.00f
        );

        assertTrue(customer instanceof Discountable);
        assertEquals(1.80f, customer.applyDiscount(2.00f), 0.001f);
    }
}