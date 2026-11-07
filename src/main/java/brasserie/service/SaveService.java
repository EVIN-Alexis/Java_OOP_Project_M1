package brasserie.service;

import brasserie.model.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SaveService {

    public boolean saveCompany(Company company, FinanceService financeService, String filePath) {

        if (company == null || filePath == null) {
            return false;
        }

        try (FileWriter writer = new FileWriter(filePath)) {

            writer.write("{\n");
            writer.write("  \"name\": \"" + company.getName() + "\",\n");
            writer.write("  \"money\": " + company.getMoney() + ",\n");
            writer.write("  \"currentDay\": " + company.getCurrentDay() + ",\n");
            writer.write("  \"reputation\": " + company.getReputation() + ",\n");
            writer.write("  \"bankrupt\": " + company.isBankrupt() + ",\n");
            writer.write("  \"extraVatPurchased\": " + company.hasExtraVat() + ",\n");
            writer.write("  \"improvedBottlingPurchased\": " + company.hasImprovedBottling() + ",\n");
            writer.write("  \"qualityControlPurchased\": " + company.hasQualityControl() + ",\n");

            // Sauvegarde des ingrédients
            writer.write("  \"ingredients\": [\n");

            for (int i = 0; i < company.getIngredients().size(); i++) {
                Ingredient ingredient = company.getIngredients().get(i);

                writer.write("    { \"ingredientName\": \""
                        + ingredient.getName()
                        + "\", \"quantity\": "
                        + ingredient.getQuantity() + " }");

                if (i < company.getIngredients().size() - 1) {
                    writer.write(",");
                }

                writer.write("\n");
            }

            writer.write("  ],\n");

            // Sauvegarde des brassins
            writer.write("  \"batches\": [\n");

            for (int i = 0; i < company.getBatches().size(); i++) {
                BrewBatch batch = company.getBatches().get(i);

                int vatId = 0;

                if (batch.getVat() != null) {
                    vatId = batch.getVat().getId();
                }

                writer.write("    { \"vatId\": " + vatId
                        + ", \"style\": \"" + batch.getRecipe().getStyle()
                        + "\", \"startDay\": " + batch.getStartDay()
                        + ", \"bottled\": " + batch.isBottled()
                        + " }");

                if (i < company.getBatches().size() - 1) {
                    writer.write(",");
                }

                writer.write("\n");
            }

            writer.write("  ],\n");

            // Sauvegarde du stock de bouteilles
            writer.write("  \"bottles\": [\n");

            for (int i = 0; i < company.getRecipes().size(); i++) {
                Recipe recipe = company.getRecipes().get(i);
                String style = recipe.getStyle();
                int quantity = company.getBottleStock(style);

                writer.write("    { \"style\": \"" + style
                        + "\", \"quantity\": " + quantity + " }");

                if (i < company.getRecipes().size() - 1) {
                    writer.write(",");
                }

                writer.write("\n");
            }

            writer.write("  ],\n");

            // Sauvegarde des commandes
            writer.write("  \"orders\": [\n");

            for (int i = 0; i < company.getOrders().size(); i++) {
                Order order = company.getOrders().get(i);
                Customer customer = order.getCustomer();

                String preferredStyles = String.join(
                        ";", customer.getPreferredStyles()
                );

                writer.write("    { \"customerName\": \"" + customer.getName()
                        + "\", \"customerType\": \"" + customer.getCustomerType()
                        + "\", \"preferredStyles\": \"" + preferredStyles
                        + "\", \"minOrderQuantity\": " + customer.getMinOrderQuantity()
                        + ", \"maxOrderQuantity\": " + customer.getMaxOrderQuantity()
                        + ", \"targetPrice\": " + customer.getTargetPrice()
                        + ", \"style\": \"" + order.getStyle()
                        + "\", \"quantity\": " + order.getQuantity()
                        + ", \"pricePerBottle\": " + order.getPricePerBottle()
                        + ", \"deadlineDay\": " + order.getDeadlineDay()
                        + ", \"status\": \"" + order.getStatus() + "\" }");

                if (i < company.getOrders().size() - 1) {
                    writer.write(",");
                }

                writer.write("\n");
            }

            writer.write("  ],\n");

            // Sauvegarde de l'historique financier
            writer.write("  \"financeRecords\": [\n");

            List<FinanceRecord> records = financeService.getRecords();

            for (int i = 0; i < records.size(); i++) {
                FinanceRecord record = records.get(i);

                writer.write("    { \"financeDay\": " + record.getDay()
                        + ", \"financeType\": \"" + record.getType()
                        + "\", \"financeAmount\": " + record.getAmount()
                        + ", \"financeDescription\": \"" + record.getDescription()
                        + "\" }");

                if (i < records.size() - 1) {
                    writer.write(",");
                }

                writer.write("\n");
            }

            writer.write("  ]\n");
            writer.write("}\n");

            return true;

        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde.");
            return false;
        }
    }

    // Lire le contenu du fichier sauvegardé
    public String readSave(String filePath) {

        StringBuilder content = new StringBuilder();

        try (FileReader reader = new FileReader(filePath)) {

            int character;

            while ((character = reader.read()) != -1) {
                content.append((char) character);
            }

        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture.");
            return null;
        }

        return content.toString();
    }

    // Charger une entreprise depuis une sauvegarde
    public Company loadCompany(String filePath) {

        String content = readSave(filePath);

        if (content == null) {
            return null;
        }

        String name = null;
        float money = -1;
        int currentDay = -1;
        int reputation = -1;
        boolean bankrupt = false;
        boolean extraVatPurchased = false;
        boolean improvedBottlingPurchased = false;
        boolean qualityControlPurchased = false;

        // On lit chaque ligne du fichier JSON
        String[] lines = content.split("\n");

        for (String line : lines) {

            line = line.trim();

            if (line.startsWith("\"name\"")) {
                int start = line.indexOf(':') + 1;
                String value = line.substring(start).trim();

                // On retire les guillemets et la virgule éventuelle
                if (value.endsWith(",")) {
                    value = value.substring(0, value.length() - 1);
                }

                if (value.startsWith("\"") && value.endsWith("\"")) {
                    name = value.substring(1, value.length() - 1);
                }
            }

            if (line.startsWith("\"money\"")) {
                String value = line.substring(line.indexOf(':') + 1)
                        .trim()
                        .replace(",", "");

                try {
                    money = Float.parseFloat(value);
                } catch (NumberFormatException e) {
                    return null;
                }
            }

            if (line.startsWith("\"currentDay\"")) {
                String value = line.substring(line.indexOf(':') + 1)
                        .trim()
                        .replace(",", "");

                try {
                    currentDay = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return null;
                }
            }

            if (line.startsWith("\"reputation\"")) {
                String value = line.substring(line.indexOf(':') + 1)
                        .trim()
                        .replace(",", "");

                try {
                    reputation = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return null;
                }
            }

            if (line.startsWith("\"bankrupt\"")) {
                String value = line.substring(line.indexOf(':') + 1)
                        .trim()
                        .replace(",", "");

                if (value.equals("true")) {
                    bankrupt = true;
                } else if (value.equals("false")) {
                    bankrupt = false;
                } else {
                    return null;
                }
            }

            if (line.startsWith("\"extraVatPurchased\"")) {
                String value = line.substring(line.indexOf(':') + 1)
                        .trim()
                        .replace(",", "");

                extraVatPurchased = Boolean.parseBoolean(value);
            }

            if (line.startsWith("\"improvedBottlingPurchased\"")) {
                String value = line.substring(line.indexOf(':') + 1)
                        .trim()
                        .replace(",", "");

                improvedBottlingPurchased = Boolean.parseBoolean(value);
            }

            if (line.startsWith("\"qualityControlPurchased\"")) {
                String value = line.substring(line.indexOf(':') + 1)
                        .trim()
                        .replace(",", "");

                qualityControlPurchased = Boolean.parseBoolean(value);
            }
        }

        // On vérifie les données avant de créer l'entreprise
        if (name == null || name.isBlank()
                || money < 0 || !Float.isFinite(money)
                || currentDay < 1) {
            return null;
        }

        // On recrée l'entreprise
        Company company = new Company(name);
        company.setMoney(money);
        company.setExtraVatPurchased(extraVatPurchased);
        company.setImprovedBottlingPurchased(improvedBottlingPurchased);
        company.setQualityControlPurchased(qualityControlPurchased);

        // On remet le jour au bon numéro
        for (int i = 1; i < currentDay; i++) {
            company.advanceDay();
        }

        company.setReputation(reputation);

        if (bankrupt) {
            company.declareBankruptcy();
        }

        // On recharge les quantités des ingrédients
        for (String line : content.split("\n")) {

            line = line.trim();

            if (line.contains("\"ingredientName\"")) {

                try {
                    int debutNom = line.indexOf("\"ingredientName\": \"")
                            + "\"ingredientName\": \"".length();

                    int finNom = line.indexOf("\"", debutNom);

                    String ingredientName =
                            line.substring(debutNom, finNom);

                    int debutQuantite = line.indexOf("\"quantity\":")
                            + "\"quantity\":".length();

                    int finQuantite = line.indexOf("}", debutQuantite);

                    float quantity = Float.parseFloat(
                            line.substring(debutQuantite, finQuantite).trim()
                    );

                    for (brasserie.model.Ingredient ingredient
                            : company.getIngredients()) {

                        if (ingredient.getName().equals(ingredientName)) {
                            ingredient.setQuantity(quantity);
                            break;
                        }
                    }

                } catch (Exception e) {
                    System.out.println(
                            "Erreur lors du chargement d'un ingrédient."
                    );
                    return null;
                }
            }
        }

        // On recharge les brassins
        for (String line : content.split("\n")) {
            line = line.trim();

            if (line.contains("\"vatId\"")) {
                try {
                    // Lecture de l'identifiant de la cuve
                    int debutVat = line.indexOf("\"vatId\":") + 8;
                    int finVat = line.indexOf(",", debutVat);
                    int vatId = Integer.parseInt(
                            line.substring(debutVat, finVat).trim()
                    );

                    // Lecture du style de bière
                    String marqueStyle = "\"style\": \"";
                    int debutStyle = line.indexOf(marqueStyle)
                            + marqueStyle.length();
                    int finStyle = line.indexOf("\"", debutStyle);
                    String style = line.substring(debutStyle, finStyle);

                    // Lecture du jour de début
                    int debutJour = line.indexOf("\"startDay\":") + 11;
                    int finJour = line.indexOf(",", debutJour);
                    int startDay = Integer.parseInt(
                            line.substring(debutJour, finJour).trim()
                    );

                    // Lecture de l'état de mise en bouteille
                    String marqueBottled = "\"bottled\":";
                    int debutBottled = line.indexOf(marqueBottled)
                            + marqueBottled.length();
                    int finBottled = line.indexOf("}", debutBottled);
                    String valeurBottled = line.substring(
                            debutBottled, finBottled
                    ).trim();

                    boolean bottled;

                    if (valeurBottled.equals("true")) {
                        bottled = true;
                    } else if (valeurBottled.equals("false")) {
                        bottled = false;
                    } else {
                        return null;
                    }

                    // On retrouve la recette correspondante
                    Recipe recipe = null;

                    for (Recipe r : company.getRecipes()) {
                        if (r.getStyle().equals(style)) {
                            recipe = r;
                            break;
                        }
                    }

                    if (recipe == null) {
                        return null;
                    }

                    // On retrouve la cuve correspondante
                    Vat vat = null;

                    if (vatId != 0) {
                        for (Vat v : company.getVats()) {
                            if (v.getId() == vatId) {
                                vat = v;
                                break;
                            }
                        }

                        if (vat == null) {
                            return null;
                        }
                    }

                    // On recrée le brassin
                    BrewBatch batch = new BrewBatch(recipe, startDay, vat);
                    batch.setBottled(bottled);

                    // On rattache le brassin à l'entreprise
                    company.getBatches().add(batch);

                    // La cuve n'est occupée que si le brassin fermente encore.
                    // Un brassin embouteillé ou dont la fermentation est finie
                    // a déjà libéré sa cuve (voir BottlingService / DayService).
                    boolean stillFermenting = !bottled
                            && !batch.isFermentationComplete(company.getCurrentDay());

                    if (vat != null && stillFermenting) {
                        if (vat.isOccupied()) {
                            return null;
                        }

                        vat.setCurrentBatch(batch);
                    }

                } catch (Exception e) {
                    System.out.println("Erreur lors du chargement d'un brassin.");
                    return null;
                }
            }
        }

        // On recharge le stock de bouteilles
        for (String line : content.split("\n")) {
            line = line.trim();

            if (line.contains("\"style\"")
                    && line.contains("\"quantity\"")
                    && !line.contains("\"customerName\"")) {
                try {
                    String marqueStyle = "\"style\": \"";
                    int debutStyle = line.indexOf(marqueStyle)
                            + marqueStyle.length();
                    int finStyle = line.indexOf("\"", debutStyle);
                    String style = line.substring(debutStyle, finStyle);

                    int debutQuantite = line.indexOf("\"quantity\":") + 11;
                    int finQuantite = line.indexOf("}", debutQuantite);

                    int quantity = Integer.parseInt(
                            line.substring(debutQuantite, finQuantite).trim()
                    );

                    if (quantity < 0) {
                        return null;
                    }

                    boolean styleExiste = false;

                    for (Recipe recipe : company.getRecipes()) {
                        if (recipe.getStyle().equals(style)) {
                            styleExiste = true;
                            break;
                        }
                    }

                    if (!styleExiste) {
                        return null;
                    }

                    company.addBottles(style, quantity);

                } catch (Exception e) {
                    System.out.println("Erreur lors du chargement du stock de bouteilles : "
                            + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
        }

        // On recharge les commandes
        for (String line : content.split("\n")) {
            line = line.trim();

            if (line.contains("\"customerName\"")) {
                try {
                    String customerName = readString(line, "customerName");
                    String stylesText = readString(line, "preferredStyles");
                    String style = readString(line, "style");
                    String statusText = readString(line, "status");

                    int minQuantity = Integer.parseInt(
                            readNumber(line, "minOrderQuantity")
                    );

                    int maxQuantity = Integer.parseInt(
                            readNumber(line, "maxOrderQuantity")
                    );

                    float targetPrice = Float.parseFloat(
                            readNumber(line, "targetPrice")
                    );

                    int quantity = Integer.parseInt(
                            readNumber(line, "quantity")
                    );

                    float pricePerBottle = Float.parseFloat(
                            readNumber(line, "pricePerBottle")
                    );

                    int deadlineDay = Integer.parseInt(
                            readNumber(line, "deadlineDay")
                    );

                    // On reconstitue la liste des styles préférés
                    java.util.List<String> preferredStyles =
                            new java.util.ArrayList<>();

                    if (stylesText != null && !stylesText.isEmpty()) {
                        for (String preferredStyle : stylesText.split(";")) {
                            preferredStyles.add(preferredStyle);
                        }
                    }

                    // On recrée le client
                    String customerType = readString(line, "customerType");
                    Customer customer;

                    if ("PROFESSIONAL".equals(customerType)) {
                        customer = new ProfessionalCustomer(
                                customerName, preferredStyles,
                                minQuantity, maxQuantity, targetPrice
                        );
                    } else if ("REGULAR".equals(customerType)) {
                        customer = new RegularCustomer(
                                customerName, preferredStyles,
                                minQuantity, maxQuantity, targetPrice
                        );
                    } else {
                    // Compatibilité avec les anciennes sauvegardes.
                    customer = new RegularCustomer(
                            customerName, preferredStyles,
                            minQuantity, maxQuantity, targetPrice
                    );
                }

                    // On recrée la commande
                    Order order = new Order(
                            customer,
                            style,
                            quantity,
                            pricePerBottle,
                            deadlineDay
                    );

                    // On restaure son statut
                    order.setStatus(Order.Status.valueOf(statusText));

                    company.getOrders().add(order);

                } catch (Exception e) {
                    System.out.println(
                            "Erreur lors du chargement d'une commande."
                    );
                    return null;
                }
            }
        }

        return company;
    }

    // Lire une chaîne de caractères dans une ligne JSON
    private String readString(String line, String key) {
        String marker = "\"" + key + "\": \"";
        int start = line.indexOf(marker);

        if (start == -1) {
            return null;
        }

        start += marker.length();

        int end = line.indexOf("\"", start);

        if (end == -1) {
            return null;
        }

        return line.substring(start, end);
    }

    // Lire un nombre dans une ligne JSON
    private String readNumber(String line, String key) {
        String marker = "\"" + key + "\": ";
        int start = line.indexOf(marker);

        if (start == -1) {
            return null;
        }

        start += marker.length();

        int end = line.indexOf(",", start);

        if (end == -1) {
            end = line.indexOf("}", start);
        }

        if (end == -1) {
            return null;
        }

        return line.substring(start, end).trim();
    }

    // Charger l'historique financier d'une sauvegarde
    public List<FinanceRecord> loadFinanceRecords(String filePath) {

        List<FinanceRecord> records = new ArrayList<>();

        String content = readSave(filePath);

        if (content == null) {
            return records;
        }

        for (String line : content.split("\n")) {

            line = line.trim();

            if (!line.contains("\"financeDay\"")) {
                continue;
            }

            try {
                int day = Integer.parseInt(readNumber(line, "financeDay"));
                String typeText = readString(line, "financeType");
                float amount = Float.parseFloat(
                        readNumber(line, "financeAmount")
                );
                String description = readString(line, "financeDescription");

                FinanceRecord.Type type =
                        FinanceRecord.Type.valueOf(typeText);

                records.add(new FinanceRecord(
                        day,
                        type,
                        amount,
                        description
                ));

            } catch (Exception e) {
                System.out.println(
                        "Une transaction financière n'a pas pu être chargée."
                );
            }
        }

        return records;
    }

    private File getSaveDirectory() {
        File directory = new File("saves");

        if (!directory.exists()) {
            directory.mkdirs();
        }

        return directory;
    }

    public List<File> listSaves() {
        File directory = getSaveDirectory();

        File[] files = directory.listFiles(
                (dir, name) -> name.endsWith(".json")
        );

        List<File> saves = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                saves.add(file);
            }
        }

        return saves;
    }

    public boolean deleteSave(File file) {
        if (file == null || !file.isFile()) {
            return false;
        }

        return file.delete();
    }
}