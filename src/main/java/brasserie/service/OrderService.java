package brasserie.service;

import brasserie.model.Company;
import brasserie.model.Customer;
import brasserie.model.InsufficientStockException;
import brasserie.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderService {

    private SalesService salesService;
    private Random random = new Random();

    // Constructeur par défaut
    public OrderService() {
        this(new SalesService());
    }

    // Constructeur permettant de partager le service de vente
    public OrderService(SalesService salesService) {
        this.salesService = salesService;
    }

    // Récupérer les commandes de l'entreprise
    public List<Order> getOrders(Company company) {
        if (company == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(company.getOrders());
    }


    // Générer entre 1 et 3 commandes pour la journée
    public List<Order> generateDailyOrders(
            Company company,
            CustomerService customerService
    ) {
        return generateDailyOrders(company, customerService, 3);
    }

    // Générer des commandes en respectant le nombre maximal autorisé
    public List<Order> generateDailyOrders(
            Company company,
            CustomerService customerService,
            int maxNewOrders
    ) {
        List<Order> newOrders = new ArrayList<>();

        if (company == null || customerService == null
                || company.isBankrupt() || maxNewOrders <= 0) {
            return newOrders;
        }

        List<Customer> customers = customerService.getCustomers();

        if (customers.isEmpty()) {
            return newOrders;
        }

        // Générer entre 1 et 3 commandes, sans dépasser la limite autorisée
        int numberOfOrders = Math.min(random.nextInt(3) + 1, maxNewOrders);

        for (int i = 0; i < numberOfOrders; i++) {

            // Choisir un client au hasard
            Customer customer = customers.get(
                    random.nextInt(customers.size())
            );

            List<String> styles = customer.getPreferredStyles();

            if (styles.isEmpty()) {
                continue;
            }

            // Choisir un style apprécié par le client
            String style = styles.get(random.nextInt(styles.size()));

            // Choisir une quantité entre le minimum et le maximum
            int min = customer.getMinOrderQuantity();
            int max = customer.getMaxOrderQuantity();

            if (min <= 0 || max < min) {
                continue;
            }

            int quantity = random.nextInt(max - min + 1) + min;

            // Prix proche du prix cible, avec une variation de 10 centimes
            float variation = (random.nextInt(21) - 10) / 100.0f;
            float price = customer.getTargetPrice() + variation;

            price = Math.max(0.01f, price);
            price = Math.round(price * 100f) / 100f;

            // Date limite dans 2 ou 3 jours
            int deadline = company.getCurrentDay() + 2 + random.nextInt(2);

            // Créer et enregistrer la commande
            Order order = createOrder(
                    company,
                    customer,
                    style,
                    quantity,
                    price,
                    deadline
            );

            if (order != null) {
                newOrders.add(order);
            }
        }

        return newOrders;
    }

    // Créer une nouvelle commande
    public Order createOrder(
            Company company,
            Customer customer,
            String style,
            int quantity,
            float pricePerBottle,
            int deadlineDay
    ) {
        if (company == null || customer == null
                || style == null || style.isBlank()) {
            return null;
        }

        if (quantity <= 0 || pricePerBottle <= 0
                || deadlineDay <= 0) {
            return null;
        }

        Order order = new Order(
                customer,
                style,
                quantity,
                pricePerBottle,
                deadlineDay
        );

        // Company conserve la liste de référence
        company.getOrders().add(order);

        return order;
    }

    // Accepter une commande en vendant les bouteilles correspondantes
    public boolean acceptOrder(Company company, Order order) {

        if (company == null || order == null) {
            return false;
        }

        // Vérifier que la commande appartient à l'entreprise
        if (!company.getOrders().contains(order)) {
            return false;
        }

        if (company.isBankrupt()) {
            return false;
        }

        // Seules les commandes en attente peuvent être acceptées
        if (order.getStatus() != Order.Status.PENDING) {
            return false;
        }

        // Vérifier la date limite
        if (order.isExpired(company.getCurrentDay())) {
            order.resolve(Order.Status.EXPIRED, company.getCurrentDay());
            company.adjustReputation(-2);
            return false;
        }

        // Effectuer la vente
        boolean saleSuccessful;
        try {
            saleSuccessful = salesService.sellBottles(
                    company,
                    order.getStyle(),
                    order.getQuantity(),
                    order.getPricePerBottle()
            );
        } catch (InsufficientStockException e) {
            System.out.println(e.getMessage());
            return false;
        }

        if (!saleSuccessful) {
            return false;
        }

        // Accepter uniquement si la vente a réussi
        order.resolve(Order.Status.ACCEPTED, company.getCurrentDay());

        return true;
    }

    // Refuser une commande
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

        return true;
    }

    // Expirer les commandes dont la date limite est dépassée
    public void expireOrders(Company company) {

        if (company == null) {
            return;
        }

        int currentDay = company.getCurrentDay();

        // 1. Marquer les commandes dépassant leur date limite
        for (Order order : company.getOrders()) {

            if (order.getStatus() == Order.Status.PENDING
                    && order.isExpired(currentDay)) {

                order.resolve(Order.Status.EXPIRED, currentDay);
                company.adjustReputation(-2);
            }
        }

        // 2. Supprimer les commandes terminées depuis au moins 2 jours
        company.getOrders().removeIf(order ->
                order.getStatus() != Order.Status.PENDING
                        && order.getResolutionDay() > 0
                        && currentDay - order.getResolutionDay() >= 2
        );
    }
}