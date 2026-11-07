package brasserie.service;

import brasserie.model.Company;
import brasserie.model.FinanceRecord;

import java.util.ArrayList;
import java.util.List;

public class FinanceService {

    private List<FinanceRecord> records;

    public FinanceService() {
        this.records = new ArrayList<>();
    }

    // Enregistre une dépense sans modifier la trésorerie
    public boolean recordExpense(Company company, float amount,
                                 String description) {
        if (!isValidRecord(company, amount, description)) {
            return false;
        }

        FinanceRecord record = new FinanceRecord(
                company.getCurrentDay(),
                FinanceRecord.Type.EXPENSE,
                amount,
                description
        );

        records.add(record);
        return true;
    }

    // Enregistre un revenu sans modifier la trésorerie
    public boolean recordRevenue(Company company, float amount,
                                 String description) {
        if (!isValidRecord(company, amount, description)) {
            return false;
        }

        FinanceRecord record = new FinanceRecord(
                company.getCurrentDay(),
                FinanceRecord.Type.REVENUE,
                amount,
                description
        );

        records.add(record);
        return true;
    }

    private boolean isValidRecord(Company company, float amount,
                                  String description) {
        return company != null
                && amount > 0
                && Float.isFinite(amount)
                && description != null
                && !description.isBlank();
    }

    public List<FinanceRecord> getRecords() {
        return new ArrayList<>(records);
    }

    public float getTotalExpenses() {
        float total = 0;

        for (FinanceRecord record : records) {
            if (record.getType() == FinanceRecord.Type.EXPENSE) {
                total += record.getAmount();
            }
        }

        return Math.round(total * 100f) / 100f;
    }

    public float getTotalRevenue() {
        float total = 0;

        for (FinanceRecord record : records) {
            if (record.getType() == FinanceRecord.Type.REVENUE) {
                total += record.getAmount();
            }
        }

        return Math.round(total * 100f) / 100f;
    }

    public float getEstimatedProfit() {
        return Math.round(
                (getTotalRevenue() - getTotalExpenses()) * 100f
        ) / 100f;
    }

    // Restaurer les transactions depuis une sauvegarde
    public void restoreRecords(List<FinanceRecord> savedRecords) {
        records.clear();

        if (savedRecords != null) {
            records.addAll(savedRecords);
        }
    }

    public void displayReport() {
        System.out.println("\n===== BILAN FINANCIER =====");

        for (FinanceRecord record : records) {
            System.out.printf(
                    "Jour %d | %-7s | %8.2f € | %s%n",
                    record.getDay(),
                    record.getType(),
                    record.getAmount(),
                    record.getDescription()
            );
        }

        System.out.printf("Total des revenus : %.2f €%n",
                getTotalRevenue());
        System.out.printf("Total des dépenses : %.2f €%n",
                getTotalExpenses());
        System.out.printf("Bénéfice estimé : %.2f €%n",
                getEstimatedProfit());
    }
}