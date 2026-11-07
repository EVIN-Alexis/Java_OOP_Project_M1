package brasserie.service;

import brasserie.model.Company;
import brasserie.model.InsufficientStockException;
import brasserie.model.Order;
import brasserie.model.ProfessionalCustomer;

public class SalesService {

    private FinanceService financeService;

    public SalesService() {
        this(new FinanceService());
    }

    public SalesService(FinanceService financeService) {
        this.financeService = financeService;
    }

    // Vendre directement des bouteilles
    public boolean sellBottles(
            Company company,
            String style,
            int quantity,
            float pricePerBottle) {

        if (company == null || style == null) {
            System.out.println("Erreur : entreprise ou style invalide.");
            return false;
        }

        if (company.isBankrupt()) {
            System.out.println("Impossible : l'entreprise est en faillite.");
            return false;
        }

        if (quantity <= 0 || pricePerBottle <= 0
                || !Float.isFinite(pricePerBottle)) {
            System.out.println("Erreur : quantité ou prix invalide.");
            return false;
        }

        int availableStock = company.getBottleStock(style);

        if (availableStock < quantity) {
            throw new InsufficientStockException(
                    style,
                    availableStock,
                    quantity
            );
        }

        float revenue = Math.round(
                quantity * pricePerBottle * 100f
        ) / 100f;

        boolean removed = company.removeBottles(style, quantity);

        if (!removed) {
            System.out.println("Erreur lors de la mise à jour du stock.");
            return false;
        }

        company.setMoney(company.getMoney() + revenue);

        financeService.recordRevenue(
                company,
                revenue,
                "Vente de " + quantity + " bouteilles de " + style
        );

        // Une vente réussie améliore la réputation
        company.adjustReputation(1);
        company.recordSaleToday();

        company.adjustReputation(1);

        System.out.println("Vente effectuée : " + revenue + " €");

        return true;
    }

    // Accepter une commande client
    public boolean acceptOrder(Company company, Order order) {

        if (company == null || order == null) {
            return false;
        }

        if (company.isBankrupt()) {
            System.out.println("Impossible : l'entreprise est en faillite.");
            return false;
        }

        if (!company.getOrders().contains(order)) {
            System.out.println("Cette commande n'appartient pas à l'entreprise.");
            return false;
        }

        if (order.getStatus() != Order.Status.PENDING) {
            System.out.println("Cette commande n'est plus en attente.");
            return false;
        }

        // Vérifier si la commande a expiré
        if (order.isExpired(company.getCurrentDay())) {
            order.resolve(Order.Status.EXPIRED, company.getCurrentDay());
            company.adjustReputation(-2);
            return false;
        }

        // La commande est acceptée uniquement si la vente réussit
        boolean success;
        try {
            success = sellBottles(
                    company,
                    order.getStyle(),
                    order.getQuantity(),
                    order.getPricePerBottle()
            );
        } catch (InsufficientStockException e) {
            System.out.println(e.getMessage());
            return false;
        }

        if (success) {
            order.resolve(Order.Status.ACCEPTED, company.getCurrentDay());

            // Bonus supplémentaire pour une commande professionnelle
            if (order.getCustomer() instanceof ProfessionalCustomer) {
                company.adjustReputation(2);
            }

            System.out.println("Commande acceptée !");
        }

        return success;
    }

    // Refuser une commande client
    public boolean refuseOrder(Company company, Order order) {

        if (company == null || order == null) {
            return false;
        }

        if (!company.getOrders().contains(order)) {
            return false;
        }

        if (order.getStatus() != Order.Status.PENDING) {
            return false;
        }

        order.resolve(Order.Status.REFUSED, company.getCurrentDay());
        company.adjustReputation(-1);
        System.out.println("Commande refusée : -1 point de réputation.");

        return true;
    }
}