package brasserie.service;

import brasserie.model.Company;
import brasserie.model.Ingredient;
import brasserie.model.Recipe;
import brasserie.model.BrewBatch;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BottlingServiceTest {

    private Company createCompanyWithEnoughIngredients() {
        Company company = new Company("Ma Brasserie");

        for (Ingredient ingredient : company.getIngredients()) {
            ingredient.setQuantity(1000f);
        }

        return company;
    }

    @Test
    void shouldRefuseBottlingBeforeFermentationEnds() {
        Company company = createCompanyWithEnoughIngredients();

        ProductionService production = new ProductionService();
        BottlingService bottling = new BottlingService();

        Recipe recipe = company.getRecipes().get(0);
        BrewBatch batch = production.launchBatch(company, recipe);

        assertNotNull(batch);

        int bottles = bottling.bottleBatch(company, batch);

        assertEquals(0, bottles);
    }

    @Test
    void shouldBottleBatchAfterFermentation() {
        Company company = createCompanyWithEnoughIngredients();

        ProductionService production = new ProductionService();
        DayService dayService = new DayService();
        BottlingService bottling = new BottlingService();

        Recipe recipe = company.getRecipes().get(0);
        BrewBatch batch = production.launchBatch(company, recipe);

        assertNotNull(batch);

        dayService.advanceDay(company);
        dayService.advanceDay(company);
        dayService.advanceDay(company);

        int bottles = bottling.bottleBatch(company, batch);

        assertTrue(bottles > 0);
    }
}