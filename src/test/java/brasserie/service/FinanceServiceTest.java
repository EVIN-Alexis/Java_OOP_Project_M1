package brasserie.service;

import brasserie.model.Company;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FinanceServiceTest {

    @Test
    void shouldCalculateExpensesCorrectly() {
        FinanceService financeService = new FinanceService();
        Company company = new Company("Brasserie");

        financeService.recordExpense(company, 120.00f, "Achat de malt");
        financeService.recordExpense(company, 30.00f, "Achat de houblon");

        assertEquals(150.00f, financeService.getTotalExpenses(), 0.01f);
    }

    @Test
    void shouldCalculateRevenueCorrectly() {
        FinanceService financeService = new FinanceService();
        Company company = new Company("Brasserie");

        financeService.recordRevenue(company, 250.00f, "Vente de bouteilles");
        financeService.recordRevenue(company, 80.00f, "Deuxième vente");

        assertEquals(330.00f, financeService.getTotalRevenue(), 0.01f);
    }

    @Test
    void shouldCalculateEstimatedProfitCorrectly() {
        FinanceService financeService = new FinanceService();
        Company company = new Company("Brasserie");

        financeService.recordExpense(company, 150.00f, "Dépenses");
        financeService.recordRevenue(company, 330.00f, "Revenus");

        assertEquals(180.00f, financeService.getEstimatedProfit(), 0.01f);
    }
}