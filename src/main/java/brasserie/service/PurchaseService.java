package brasserie.service;

import brasserie.model.Company;
import brasserie.model.Ingredient;
import brasserie.service.FinanceService;

public class PurchaseService {

    private FinanceService financeService;

    public PurchaseService() {
        this(new FinanceService());
    }

    public PurchaseService(FinanceService financeService) {
        this.financeService = financeService;
    }

    public boolean purchaseIngredient(Company company, Ingredient ingredient, int quantity) {

        // Vérifier que les objets existent
        if (company == null || ingredient == null) {
            return false;
        }

        // Une entreprise en faillite ne peut plus acheter
        if (company.isBankrupt()) {
            return false;
        }

        // La quantité achetée doit être positive
        if (quantity <= 0) {
            return false;
        }

        // Calculer le coût total de l'achat
        float totalCost = Math.round(
                ingredient.getUnitPrice() * quantity * 100f
        ) / 100f;

        // Vérifier que l'entreprise a assez d'argent
        if (company.getMoney() < totalCost) {
            return false;
        }

        // Retirer l'argent de la trésorerie
        company.setMoney(company.getMoney() - totalCost);

        // Enregistrer la dépense dans le bilan financier
        financeService.recordExpense(
                company,
                totalCost,
                "Achat de " + ingredient.getName()
        );

        // Ajouter les ingrédients au stock
        ingredient.setQuantity(ingredient.getQuantity() + quantity);

        return true;
    }
}