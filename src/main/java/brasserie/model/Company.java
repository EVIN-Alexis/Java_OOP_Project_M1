package brasserie.model;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Company {

    private String name;
    private float money;
    private int currentDay;
    private int reputation;
    private boolean bankrupt;
    private boolean extraVatPurchased;
    private boolean improvedBottlingPurchased;
    private boolean qualityControlPurchased;
    private boolean saleMadeToday;

    private List<Ingredient> ingredients;
    private List<Recipe> recipes;
    private List<BrewBatch> batches;
    private List<Vat> vats;
    private List<Order> orders;

    private Map<String, Integer> bottleStock;

    public Company(String name) {
        this.name = name;
        this.money = 1500.00f;
        this.currentDay = 1;
        this.reputation = 25;
        this.bankrupt = false;
        this.saleMadeToday = false;

        this.ingredients = new ArrayList<>();
        this.recipes = new ArrayList<>();
        this.batches = new ArrayList<>();
        this.vats = new ArrayList<>();
        this.orders = new ArrayList<>();

        this.bottleStock = new HashMap<>();
        bottleStock.put("Blonde", 0);
        bottleStock.put("IPA", 0);
        bottleStock.put("Rousse", 0);
        bottleStock.put("Brune", 0);

        initializeIngredients();
        initializeRecipes();
    }

    private void initializeIngredients() {
        ingredients.add(new Ingredient("Malt standard", "kg", 2.50f, 0f));
        ingredients.add(new Ingredient("Malt spécial", "kg", 3.50f, 0f));
        ingredients.add(new Ingredient("Houblon standard", "kg", 25.00f, 0f));
        ingredients.add(new Ingredient("Houblon premium", "kg", 40.00f, 0f));
        ingredients.add(new Ingredient("Levure standard", "sachet", 5.00f, 0f));

        vats.add(new Vat(1, 100f));
        vats.add(new Vat(2, 100f));
    }

    private void initializeRecipes() {
        recipes.add(new Recipe(
                "Blonde", "Blonde",
                11f, 0f, 0.15f, 0f, 3, 60f));

        recipes.add(new Recipe(
                "IPA", "IPA",
                11f, 0f, 0f, 0.25f, 3, 60f));

        recipes.add(new Recipe(
                "Rousse", "Rousse",
                10f, 2f, 0.18f, 0f, 3, 60f));

        recipes.add(new Recipe(
                "Brune", "Brune",
                9f, 3f, 0.12f, 0f, 3, 60f));
    }

    public String getName() {
        return name;
    }

    public float getMoney() {
        return money;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public int getReputation() {
        return reputation;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public List<BrewBatch> getBatches() {
        return batches;
    }

    public boolean isBankrupt() {
        return bankrupt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMoney(float money) {
        if (money >= 0) {
            this.money = Math.round(money * 100f) / 100f;
        }
    }

    public void setReputation(int reputation) {
        if (reputation < 0) {
            this.reputation = 0;
        } else if (reputation > 100) {
            this.reputation = 100;
        } else {
            this.reputation = reputation;
        }
    }

    // Faire évoluer la réputation sans dépasser les limites
    public void adjustReputation(int points) {
        setReputation(this.reputation + points);
    }

    // Enregistrer qu'une vente a eu lieu aujourd'hui
    public void recordSaleToday() {
        saleMadeToday = true;
    }

    public boolean hasMadeSaleToday() {
        return saleMadeToday;
    }

    public void resetDailySales() {
        saleMadeToday = false;
    }

    public void advanceDay() {
        currentDay++;
    }

    public void declareBankruptcy() {
        bankrupt = true;
    }

    public List<Vat> getVats() {
        return vats;
    }

    public int getBottleStock(String style) {
        return bottleStock.getOrDefault(style, 0);
    }

    public void addBottles(String style, int quantity) {
        if (style != null && quantity > 0) {
            bottleStock.put(
                    style,
                    getBottleStock(style) + quantity
            );
        }
    }

    public boolean removeBottles(String style, int quantity) {
        if (style == null || quantity <= 0) {
            return false;
        }

        int stock = getBottleStock(style);

        if (stock < quantity) {
            return false;
        }

        bottleStock.put(style, stock - quantity);
        return true;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public boolean hasExtraVat() {
        return extraVatPurchased;
    }

    public boolean hasImprovedBottling() {
        return improvedBottlingPurchased;
    }

    public boolean hasQualityControl() {
        return qualityControlPurchased;
    }

    public void setExtraVatPurchased(boolean purchased) {
        extraVatPurchased = purchased;

        if (purchased) {
            boolean vatExists = false;

            for (Vat vat : vats) {
                if (vat.getId() == 3) {
                    vatExists = true;
                    break;
                }
            }

            if (!vatExists) {
                vats.add(new Vat(3, 100f));
            }
        }
    }

    public void setImprovedBottlingPurchased(boolean purchased) {
        improvedBottlingPurchased = purchased;
    }

    public void setQualityControlPurchased(boolean purchased) {
        qualityControlPurchased = purchased;
    }
}