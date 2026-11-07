package brasserie.service;

import brasserie.model.BrewBatch;
import brasserie.model.Company;
import brasserie.model.Order;
import brasserie.model.Vat;

import java.util.ArrayList;
import java.util.List;

public class DayService {

    private FinanceService financeService;
    private OrderService orderService;
    private CustomerService customerService;

    public DayService() {
        this(new FinanceService());
    }


    public DayService(FinanceService financeService) {
        this.financeService = financeService;
        this.orderService = new OrderService(
                new SalesService(financeService)
        );
        this.customerService = new CustomerService();
    }

    public FinanceService getFinanceService() {
        return financeService;
    }

    public void advanceDay(Company company) {

        if (company == null) {
            System.out.println("Erreur : entreprise inexistante.");
            return;
        }

        if (company.isBankrupt()) {
            System.out.println(
                    "Impossible : l'entreprise est en faillite."
            );
            return;
        }

        // Bonus quotidien si au moins une vente a été réalisée
        // pendant la journée qui se termine.
        if (company.hasMadeSaleToday()) {
            company.adjustReputation(1);

            System.out.println(
                    "Bonne journée commerciale : +1 point de réputation !"
            );
        }

        // Réinitialiser le compteur avant la nouvelle journée
        company.resetDailySales();

        // On passe au jour suivant.
        company.advanceDay();

        int currentDay = company.getCurrentDay();

        System.out.println("\n=== NOUVELLE JOURNÉE ===");
        System.out.println("Jour actuel : " + currentDay);

        // Tous les 7 jours, on paie les frais hebdomadaires.
        if (currentDay % 7 == 0) {

            float fraisHebdomadaires = 60.00f;

            if (company.getMoney() >= fraisHebdomadaires) {

                company.setMoney(
                        company.getMoney() - fraisHebdomadaires
                );

                financeService.recordExpense(
                        company,
                        fraisHebdomadaires,
                        "Frais hebdomadaires (loyer, eau, électricité, entretien)"
                );

                System.out.println(
                        "Frais hebdomadaires payés : "
                                + fraisHebdomadaires + " €"
                );

            } else {
                System.out.println(
                        "Attention : fonds insuffisants pour payer "
                                + "les frais hebdomadaires de 60 €."
                );
            }
        }

        // On vérifie les brassins en cours.
        for (BrewBatch batch : company.getBatches()) {

            if (batch.isFermentationComplete(currentDay)) {

                Vat vat = batch.getVat();

                // On libère la cuve si elle contient encore ce brassin.
                if (vat != null && vat.getCurrentBatch() == batch) {
                    vat.empty();

                    System.out.println(
                            "Le brassin " + batch.getRecipe().getStyle()
                                    + " a terminé sa fermentation."
                    );

                    System.out.println(
                            "La cuve " + vat.getId()
                                    + " est maintenant libre."
                    );
                }
            }
        }

        // On expire les commandes dont la date limite est dépassée.
        orderService.expireOrders(company);

        // On compte les commandes encore en attente.
        int commandesEnAttente = countPendingOrders(company);

        // On limite le nombre de commandes en attente à 4.
        int placesDisponibles = 4 - commandesEnAttente;

        List<Order> newOrders;

        if (placesDisponibles > 0) {

            // On transmet le nombre de places à OrderService.
            newOrders = orderService.generateDailyOrders(
                    company,
                    customerService,
                    placesDisponibles
            );

        } else {
            newOrders = new ArrayList<>();

            System.out.println(
                    "Aucune nouvelle commande : les 4 places sont occupées."
            );
        }

        System.out.println(
                "Commandes en attente : "
                        + countPendingOrders(company) + "/4"
        );

        System.out.println(
                "Nouvelles commandes reçues : " + newOrders.size()
        );

        for (Order order : newOrders) {
            System.out.println(
                    "- " + order.getCustomer().getName()
                            + " : " + order.getQuantity()
                            + " bouteilles de " + order.getStyle()
                            + " à " + order.getPricePerBottle() + " €"
            );
        }
    }

    // Compter uniquement les commandes en attente et non expirées.
    private int countPendingOrders(Company company) {

        int count = 0;

        for (Order order : company.getOrders()) {
            if (order.getStatus() == Order.Status.PENDING
                    && !order.isExpired(company.getCurrentDay())) {
                count++;
            }
        }

        return count;
    }
}