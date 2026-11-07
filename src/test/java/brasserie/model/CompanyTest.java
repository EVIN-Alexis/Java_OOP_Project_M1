package brasserie.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompanyTest {

    @Test
    void shouldInitializeCompanyCorrectly() {
        Company company = new Company("Ma Brasserie");

        assertEquals("Ma Brasserie", company.getName());
        assertEquals(1500.0f, company.getMoney(), 0.01f);
        assertEquals(1, company.getCurrentDay());
        assertEquals(25, company.getReputation());
        assertFalse(company.isBankrupt());
    }

    @Test
    void shouldInitializeIngredientsAndRecipes() {
        Company company = new Company("Ma Brasserie");

        assertFalse(company.getIngredients().isEmpty());
        assertFalse(company.getRecipes().isEmpty());
    }

    @Test
    void shouldInitializeVats() {
        Company company = new Company("Ma Brasserie");

        assertEquals(2, company.getVats().size());

        for (Vat vat : company.getVats()) {
            assertFalse(vat.isOccupied());
        }
    }

    @Test
    void shouldAdvanceDay() {
        Company company = new Company("Ma Brasserie");

        company.advanceDay();

        assertEquals(2, company.getCurrentDay());
    }

    @Test
    void shouldUpdateMoneyAndReputation() {
        Company company = new Company("Ma Brasserie");

        company.setMoney(1250.0f);
        company.setReputation(60);

        assertEquals(1250.0f, company.getMoney(), 0.01f);
        assertEquals(60, company.getReputation());
    }

    @Test
    void shouldAddBottlesToStock() {
        Company company = new Company("Ma Brasserie");

        company.addBottles("Blonde", 120);

        assertEquals(120, company.getBottleStock("Blonde"));
    }

    @Test
    void shouldRemoveBottlesFromStock() {
        Company company = new Company("Ma Brasserie");

        company.addBottles("Blonde", 120);
        company.removeBottles("Blonde", 20);

        assertEquals(100, company.getBottleStock("Blonde"));
    }
}