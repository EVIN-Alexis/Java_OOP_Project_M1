package brasserie.service;

import brasserie.model.Company;
import brasserie.model.Ingredient;
import brasserie.model.Recipe;
import brasserie.model.BrewBatch;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductionServiceTest {

    private Company createCompanyWithEnoughIngredients() {
        Company company = new Company("Ma Brasserie");

        for (Ingredient ingredient : company.getIngredients()) {
            ingredient.setQuantity(1000f);
        }

        return company;
    }

    @Test
    void shouldLaunchFirstBatch() {
        Company company = createCompanyWithEnoughIngredients();
        ProductionService service = new ProductionService();
        Recipe recipe = company.getRecipes().get(0);

        BrewBatch batch = service.launchBatch(company, recipe);

        assertNotNull(batch);
        assertEquals(1, company.getBatches().size());
    }

    @Test
    void shouldLaunchTwoBatches() {
        Company company = createCompanyWithEnoughIngredients();
        ProductionService service = new ProductionService();
        Recipe recipe = company.getRecipes().get(0);

        BrewBatch firstBatch = service.launchBatch(company, recipe);
        BrewBatch secondBatch = service.launchBatch(company, recipe);

        assertNotNull(firstBatch);
        assertNotNull(secondBatch);
        assertEquals(2, company.getBatches().size());
    }

    @Test
    void shouldRefuseThirdBatchWhenAllVatsAreOccupied() {
        Company company = createCompanyWithEnoughIngredients();
        ProductionService service = new ProductionService();
        Recipe recipe = company.getRecipes().get(0);

        service.launchBatch(company, recipe);
        service.launchBatch(company, recipe);

        BrewBatch thirdBatch = service.launchBatch(company, recipe);

        assertNull(thirdBatch);
        assertEquals(2, company.getBatches().size());
    }
}