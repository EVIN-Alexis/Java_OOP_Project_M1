package brasserie.ui;

import brasserie.model.*;
import brasserie.service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.Locale;

import static brasserie.ui.CraftTheme.FOREST;
import static brasserie.ui.CraftTheme.FOREST_LIGHT;

public class MainWindow extends JFrame {

    private Company company;
    private DayService dayService;
    private ProductionService productionService;
    private JComboBox<Recipe> recipeComboBox;
    private JLabel dayLabel;
    private JLabel moneyLabel;
    private JLabel reputationLabel;
    private JLabel alertLabel;
    private JTextArea productionArea;
    private PurchaseService purchaseService;
    private JTable stockTable;
    private javax.swing.table.DefaultTableModel stockTableModel;
    private JLabel stockMoneyLabel;
    private JSpinner quantitySpinner;
    private JComboBox<Ingredient> ingredientComboBox;
    private BottlingService bottlingService;
    private JComboBox<BrewBatch> batchComboBox;
    private JTextArea bottlingArea;
    private JLabel bottlingMoneyLabel;
    private JTextArea recipeDetailsArea;
    private SalesService salesService;
    private JTable ordersTable;
    private DefaultTableModel ordersTableModel;
    private FinanceService financeService;
    private JTable bottleStockTable;
    private DefaultTableModel bottleStockTableModel;
    private JLabel financeMoneyLabel;
    private JLabel financeRevenueLabel;
    private JLabel financeExpenseLabel;
    private JLabel financeProfitLabel;
    private JTable financeTable;
    private DefaultTableModel financeTableModel;
    private JLabel extraVatStatusLabel;
    private JLabel improvedBottlingStatusLabel;
    private JLabel qualityControlStatusLabel;
    private JButton extraVatButton;
    private JButton improvedBottlingButton;
    private JButton qualityControlButton;
    private SaveService saveService;
    private JTabbedPane tabs;
    private DefaultListModel<File> saveListModel;
    private JList<File> saveList;
    private String currentSaveName = "Aucune sauvegarde";
    private JLabel saveNameLabel;

    public MainWindow(Company company, DayService dayService) {

        this.company = company;
        this.dayService = dayService;
        this.productionService = new ProductionService();
        this.financeService = dayService.getFinanceService();
        this.purchaseService = new PurchaseService(financeService);
        this.bottlingService = new BottlingService();
        this.salesService = new SalesService(financeService);
        this.saveService = new SaveService();

        setTitle("MalTown - Gestion de ma brasserie");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Création des onglets
        tabs = new JTabbedPane();
        tabs.addTab("Tableau de bord", createDashboard());
        tabs.addTab("Production", createProductionPanel());
        tabs.addTab("Stocks et achats", createStockPanel());
        tabs.addTab("Embouteillage", createBottlingPanel());
        tabs.addTab("Ventes / commandes", createSalesPanel());
        tabs.addTab("Finances", createFinancePanel());
        tabs.addTab("Améliorations", createUpgradePanel());
        tabs.addTab("Sauvegardes", createSavePanel());

        add(tabs);

        // Application du thème aux autres onglets uniquement
        getContentPane().setBackground(CraftTheme.CREAM);

        for (int i = 1; i < tabs.getTabCount(); i++) {
            CraftTheme.apply(tabs.getComponentAt(i));
        }

        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 3) refreshBottling();
            if (tabs.getSelectedIndex() == 2) refreshStock();
            if (tabs.getSelectedIndex() == 4) refreshOrders();
            if (tabs.getSelectedIndex() == 5) refreshFinancePanel();
            if (tabs.getSelectedIndex() == 6) refreshUpgradePanel();
        });

        // Afficher les informations initiales
        refreshDashboard();
    }

    // Créer le tableau de bord
    private JPanel createDashboard() {

        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(CraftTheme.CREAM);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Titre
        JLabel title = new JLabel("Tableau de bord");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(CraftTheme.TEXT);

        // Cartes principales
        JPanel cardsPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        cardsPanel.setBackground(CraftTheme.CREAM);

        dayLabel = new JLabel();
        moneyLabel = new JLabel();
        reputationLabel = new JLabel();
        saveNameLabel = new JLabel();

        JPanel mainCard = new JPanel();
        mainCard.setLayout(new BoxLayout(mainCard, BoxLayout.Y_AXIS));
        mainCard.setBackground(FOREST);
        mainCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel brand = new JLabel("MalTown");
        brand.setFont(new Font("SansSerif", Font.BOLD, 27));
        brand.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Gestion de brasserie");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(230, 235, 225));

        dayLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        dayLabel.setForeground(Color.WHITE);
        dayLabel.setOpaque(true);
        dayLabel.setBackground(FOREST_LIGHT);
        dayLabel.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        dayLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        moneyLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        moneyLabel.setForeground(Color.WHITE);

        JLabel moneyTitle = new JLabel("Trésorerie");
        moneyTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        moneyTitle.setForeground(new Color(230, 235, 225));

        mainCard.add(brand);
        mainCard.add(Box.createVerticalStrut(3));
        mainCard.add(subtitle);
        mainCard.add(Box.createVerticalStrut(15));
        mainCard.add(dayLabel);
        mainCard.add(Box.createVerticalStrut(18));
        mainCard.add(moneyLabel);
        mainCard.add(moneyTitle);

        // Carte réputation
        JPanel reputationCard = createMetricCard(
                "Réputation",
                reputationLabel,
                "Image de votre brasserie",
                FOREST_LIGHT
        );

        cardsPanel.add(mainCard);
        cardsPanel.add(reputationCard);

        // Carte sauvegarde
        JPanel saveCard = new JPanel(new BorderLayout());
        saveCard.setBackground(CraftTheme.CARD);
        saveCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(222, 211, 193), 1, true
                ),
                BorderFactory.createEmptyBorder(15, 18, 15, 18)
        ));

        mainCard.setBackground(CraftTheme.FOREST);
        mainCard.setOpaque(true);

        JLabel saveTitle = new JLabel("Sauvegarde actuelle");
        saveTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        saveTitle.setForeground(CraftTheme.TEXT);

        saveNameLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        saveNameLabel.setForeground(CraftTheme.MUTED);

        JPanel saveContent = new JPanel();
        saveContent.setLayout(new BoxLayout(saveContent, BoxLayout.Y_AXIS));
        saveContent.setBackground(CraftTheme.CARD);
        saveContent.add(saveTitle);
        saveContent.add(Box.createVerticalStrut(6));
        saveContent.add(saveNameLabel);

        saveCard.add(saveContent, BorderLayout.CENTER);

        // Zone inférieure
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 15));
        bottomPanel.setBackground(CraftTheme.CREAM);

        alertLabel = new JLabel("Votre brasserie est en activité.");
        alertLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        alertLabel.setForeground(CraftTheme.TEXT);

        JButton nextDayButton = new JButton("Terminer la journée  →");
        nextDayButton.setBackground(FOREST);
        nextDayButton.setForeground(Color.WHITE);
        nextDayButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        nextDayButton.setFocusPainted(false);
        nextDayButton.setBorder(BorderFactory.createEmptyBorder(
                14, 20, 14, 20
        ));

        nextDayButton.addActionListener(e -> {
            dayService.advanceDay(company);
            refreshDashboard();
            refreshProduction();
            refreshBottling();
            refreshOrders();
        });

        bottomPanel.add(alertLabel, BorderLayout.NORTH);
        bottomPanel.add(nextDayButton, BorderLayout.SOUTH);

        // Organisation générale
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(CraftTheme.CREAM);
        centerPanel.add(cardsPanel, BorderLayout.NORTH);
        centerPanel.add(saveCard, BorderLayout.CENTER);

        panel.add(title, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMetricCard(
            String title,
            JLabel valueLabel,
            String description,
            Color background
    ) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(background);
        card.setBorder(BorderFactory.createEmptyBorder(
                20, 20, 20, 20
        ));

        card.setBackground(background);
        card.setOpaque(true);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        valueLabel.setForeground(Color.WHITE);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descriptionLabel.setForeground(new Color(235, 240, 230));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(25));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(descriptionLabel);

        return card;
    }

    private JPanel createProductionPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JLabel title = new JLabel("Gestion de la production");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        // Liste des recettes disponibles
        recipeComboBox = new JComboBox<>();

        for (Recipe recipe : company.getRecipes()) {
            recipeComboBox.addItem(recipe);
        }

        JPanel launchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Affichage des ingrédients nécessaires pour la recette
        recipeDetailsArea = new JTextArea(7, 35);
        recipeDetailsArea.setEditable(false);
        recipeDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        recipeDetailsArea.setBackground(new Color(245, 245, 245));

        JScrollPane recipeScrollPane = new JScrollPane(recipeDetailsArea);

        // Actualiser les détails quand on change de recette
        recipeComboBox.addActionListener(e -> updateRecipeDetails());

        launchPanel.add(new JLabel("Choisir une recette :"));
        launchPanel.add(recipeComboBox);

        JButton launchButton = new JButton("Lancer le brassin");

        launchButton.addActionListener(e -> launchProduction());

        launchPanel.add(launchButton);

        // Zone d'affichage des cuves et des brassins
        productionArea = new JTextArea();
        productionArea.setEditable(false);
        productionArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(productionArea);

        JButton refreshButton = new JButton("Actualiser");

        refreshButton.addActionListener(e -> refreshProduction());

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

        bottomPanel.add(launchPanel, BorderLayout.NORTH);
        bottomPanel.add(recipeScrollPane, BorderLayout.CENTER);
        bottomPanel.add(refreshButton, BorderLayout.SOUTH);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        updateRecipeDetails();
        refreshProduction();

        return panel;
    }

    private void updateRecipeDetails() {

        Recipe recipe = (Recipe) recipeComboBox.getSelectedItem();

        if (recipe == null) {
            recipeDetailsArea.setText("Aucune recette sélectionnée.");
            return;
        }

        StringBuilder text = new StringBuilder();

        text.append("=== RECETTE : ")
                .append(recipe.getCommercialName())
                .append(" ===\n\n");

        text.append("Volume produit : ")
                .append(recipe.getVolumeLiters())
                .append(" L\n\n");

        text.append("Ingrédients nécessaires :\n");
        text.append("- Malt standard : ")
                .append(recipe.getStandardMaltKg())
                .append(" kg\n");

        text.append("- Malt spécial : ")
                .append(recipe.getSpecialMaltKg())
                .append(" kg\n");

        text.append("- Houblon standard : ")
                .append(recipe.getStandardHopsKg())
                .append(" kg\n");

        text.append("- Houblon premium : ")
                .append(recipe.getPremiumHopsKg())
                .append(" kg\n");

        text.append("- Levure standard : ")
                .append(recipe.getYeastSachets())
                .append(" sachets\n");

        recipeDetailsArea.setText(text.toString());
    }

    private void launchProduction() {

        Recipe recipe = (Recipe) recipeComboBox.getSelectedItem();

        if (recipe == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Aucune recette disponible."
            );
            return;
        }

        var batch = productionService.launchBatch(company, recipe);

        System.out.println("=== DEBUG PRODUCTION ===");
        System.out.println("Nombre de brassins : " + company.getBatches().size());

        for (var vat : company.getVats()) {
            System.out.println(
                    "Cuve " + vat.getId()
                            + " occupée : " + vat.isOccupied()
            );
        }

        if (batch != null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Brassin lancé avec succès !\n"
                            + "Bière : " + recipe.getStyle() + "\n"
                            + "Volume : " + batch.getVolumeLiters() + " L"
            );

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Impossible de lancer le brassin.\n"
                            + "Vérifie les cuves et les ingrédients."
            );
        }

        refreshProduction();
        refreshBottling();
    }

    private void refreshProduction() {

        StringBuilder texte = new StringBuilder();

        texte.append("=== MES CU VES ===\n\n");

        for (var vat : company.getVats()) {

            texte.append("Cuve n°")
                    .append(vat.getId())
                    .append(" : ");

            if (vat.isOccupied()) {
                texte.append("Occupée");
            } else {
                texte.append("Libre");
            }

            texte.append("\n");
        }

        texte.append("\n=== MES BRASSINS ===\n\n");

        if (company.getBatches().isEmpty()) {
            texte.append("Aucun brassin pour le moment.\n");
        }

        for (var batch : company.getBatches()) {

            texte.append("Bière : ")
                    .append(batch.getRecipe().getStyle())
                    .append("\n");

            texte.append("Jour de lancement : ")
                    .append(batch.getStartDay())
                    .append("\n");

            int joursEcoules =
                    company.getCurrentDay() - batch.getStartDay();

            int joursRestants = Math.max(0, 3 - joursEcoules);

            if (batch.isBottled()) {
                texte.append("État : Déjà embouteillé\n");
            } else if (joursRestants == 0) {
                texte.append("État : Fermentation terminée\n");
            } else {
                texte.append("État : En fermentation\n");
                texte.append("Jours restants : ")
                        .append(joursRestants)
                        .append("\n");
            }

            texte.append("------------------------------\n");
        }

        productionArea.setText(texte.toString());
    }

    private JPanel createStockPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Gestion des stocks et des achats");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        stockMoneyLabel = new JLabel();
        stockMoneyLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(stockMoneyLabel, BorderLayout.SOUTH);

        // Tableau des ingrédients
        String[] columns = {
                "Ingrédient", "Unité", "Prix unitaire", "Quantité en stock"
        };

        stockTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        stockTable = new JTable(stockTableModel);
        stockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane ingredientsScrollPane = new JScrollPane(stockTable);

        // Choix de l'ingrédient à acheter
        ingredientComboBox = new JComboBox<>();


        stockTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = stockTable.getSelectedRow();

                if (selectedRow != -1) {
                    ingredientComboBox.setSelectedIndex(
                            stockTable.convertRowIndexToModel(selectedRow)
                    );
                }
            }
        });

        ingredientComboBox.addActionListener(e -> {
            int selectedIndex = ingredientComboBox.getSelectedIndex();

            if (selectedIndex != -1 && stockTable != null
                    && selectedIndex < stockTable.getRowCount()) {
                stockTable.setRowSelectionInterval(
                        selectedIndex, selectedIndex
                );
            }
        });

        quantitySpinner = new JSpinner(
                new SpinnerNumberModel(1, 1, 10000, 1)
        );

        JButton buyButton = new JButton("Acheter");
        buyButton.addActionListener(e -> buyIngredient());

        JPanel purchasePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        purchasePanel.add(new JLabel("Ingrédient :"));
        purchasePanel.add(ingredientComboBox);
        purchasePanel.add(new JLabel("Quantité :"));
        purchasePanel.add(quantitySpinner);
        purchasePanel.add(buyButton);

        // Tableau du stock de bouteilles
        String[] bottleColumns = {
                "Type de bière", "Bouteilles disponibles"
        };

        bottleStockTableModel = new DefaultTableModel(bottleColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Tableau du stock de bouteilles
        String[] stockColumns = {"Type de bière", "Bouteilles disponibles"};

        bottleStockTableModel = new DefaultTableModel(stockColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bottleStockTable = new JTable(bottleStockTableModel);
        bottleStockTable.setRowHeight(24);

        JPanel bottlePanel = new JPanel(new BorderLayout(5, 5));
        bottlePanel.add(
                new JLabel("Stock de bouteilles par type de bière"),
                BorderLayout.NORTH
        );
        bottlePanel.add(
                new JScrollPane(bottleStockTable),
                BorderLayout.CENTER
        );

        // Les deux tableaux sont affichés l'un au-dessus de l'autre
        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        tablesPanel.add(ingredientsScrollPane);
        tablesPanel.add(bottlePanel);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tablesPanel, BorderLayout.CENTER);
        panel.add(purchasePanel, BorderLayout.SOUTH);

        refreshStock();
        refreshBottleStock();

        return panel;
    }

    private void refreshIngredientComboBox() {
        ingredientComboBox.removeAllItems();

        for (Ingredient ingredient : company.getIngredients()) {
            ingredientComboBox.addItem(ingredient);
        }
    }

    private void buyIngredient() {
        Ingredient ingredient =
                (Ingredient) ingredientComboBox.getSelectedItem();

        int quantity = (Integer) quantitySpinner.getValue();

        if (ingredient == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Aucun ingrédient disponible."
            );
            return;
        }

        float totalCost = Math.round(
                ingredient.getUnitPrice() * quantity * 100f
        ) / 100f;

        // Vérifier le budget avant de demander confirmation.
        if (company.getMoney() < totalCost) {
            JOptionPane.showMessageDialog(
                    this,
                    String.format(
                            Locale.FRANCE,
                            "Fonds insuffisants !\nCoût : %.2f €\nTrésorerie : %.2f €",
                            totalCost,
                            company.getMoney()
                    ),
                    "Achat impossible",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                String.format(
                        Locale.FRANCE,
                        "Acheter %d %s de %s pour %.2f € ?",
                        quantity,
                        ingredient.getUnit(),
                        ingredient.getName(),
                        totalCost
                ),
                "Confirmer l'achat",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success = purchaseService.purchaseIngredient(
                company,
                ingredient,
                quantity
        );

        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Achat effectué avec succès !"
            );

            refreshStock();
            refreshDashboard();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "L'achat a échoué. Vérifie la trésorerie et l'état de l'entreprise.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void refreshStock() {
        refreshIngredientComboBox();
        stockTableModel.setRowCount(0);

        for (Ingredient ingredient : company.getIngredients()) {
            stockTableModel.addRow(new Object[] {
                    ingredient.getName(),
                    ingredient.getUnit(),
                    String.format(
                            Locale.FRANCE,
                            "%.2f €",
                            ingredient.getUnitPrice()
                    ),
                    String.format(
                            Locale.FRANCE,
                            "%.2f",
                            ingredient.getQuantity()
                    )
            });
        }

        stockMoneyLabel.setText(
                String.format(
                        Locale.FRANCE,
                        "Trésorerie disponible : %.2f €",
                        company.getMoney()
                )
        );

        refreshBottleStock();
    }

    private JPanel createBottlingPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Gestion de l'embouteillage");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        bottlingMoneyLabel = new JLabel();
        bottlingMoneyLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel topPanel = new JPanel(new BorderLayout(5, 10));
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(bottlingMoneyLabel, BorderLayout.SOUTH);

        batchComboBox = new JComboBox<>();
        batchComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                if (value instanceof BrewBatch batch) {
                    setText(batch.getRecipe().getStyle()
                            + " - lancé au jour " + batch.getStartDay());
                }

                return this;
            }
        });

        JButton bottleButton = new JButton("Embouteiller le brassin");
        bottleButton.addActionListener(e -> bottleSelectedBatch());

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.add(new JLabel("Brassin disponible :"));
        actionPanel.add(batchComboBox);
        actionPanel.add(bottleButton);

        bottlingArea = new JTextArea();
        bottlingArea.setEditable(false);
        bottlingArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(bottlingArea), BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        refreshBottling();
        return panel;
    }

    private void bottleSelectedBatch() {
        BrewBatch batch =
                (BrewBatch) batchComboBox.getSelectedItem();

        if (batch == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Aucun brassin prêt à être embouteillé."
            );
            return;
        }

        int bottleCount = bottlingService.bottleBatch(company, batch);

        if (bottleCount > 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Embouteillage réussi !\n"
                            + "Bière : " + batch.getRecipe().getStyle() + "\n"
                            + "Bouteilles produites : " + bottleCount
            );
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Embouteillage impossible.\n"
                            + "Vérifie que la fermentation est terminée "
                            + "et que le brassin n'a pas déjà été embouteillé."
            );
        }

        refreshBottling();
        refreshProduction();
        refreshDashboard();
        refreshBottleStock();
    }

    private void refreshBottling() {
        batchComboBox.removeAllItems();

        StringBuilder text = new StringBuilder();
        text.append("=== BRASSINS ===\n\n");

        boolean available = false;

        for (BrewBatch batch : company.getBatches()) {
            text.append("Bière : ")
                    .append(batch.getRecipe().getStyle())
                    .append("\n");

            text.append("Volume : ")
                    .append(batch.getVolumeLiters())
                    .append(" L\n");

            if (batch.isBottled()) {
                text.append("État : Embouteillé\n");
            } else if (batch.isFermentationComplete(company.getCurrentDay())) {
                text.append("État : Prêt à embouteiller\n");
                batchComboBox.addItem(batch);
                available = true;
            } else {
                int remaining = Math.max(
                        0,
                        3 - (company.getCurrentDay() - batch.getStartDay())
                );

                text.append("État : En fermentation\n");
                text.append("Jours restants : ")
                        .append(remaining)
                        .append("\n");
            }

            text.append("------------------------------\n");
        }

        if (company.getBatches().isEmpty()) {
            text.append("Aucun brassin pour le moment.\n");
        }

        if (!available) {
            text.append("\nAucun brassin prêt actuellement.\n");
        }

        bottlingArea.setText(text.toString());

        bottlingMoneyLabel.setText(
                String.format(
                        Locale.FRANCE,
                        "Trésorerie : %.2f €",
                        company.getMoney()
                )
        );
    }

    private JPanel createUpgradePanel() {

        JPanel panel = new JPanel(new GridLayout(3, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Amélioration 1 : cuve supplémentaire
        JPanel vatPanel = new JPanel(new BorderLayout(10, 10));
        vatPanel.setBorder(BorderFactory.createTitledBorder("Cuve supplémentaire"));

        vatPanel.add(
                new JLabel("Ajoute une troisième cuve de 100 litres. Prix : 2 000 €"),
                BorderLayout.CENTER
        );

        extraVatStatusLabel = new JLabel();
        extraVatButton = new JButton("Acheter - 2 000 €");
        extraVatButton.addActionListener(e -> buyUpgrade("vat"));

        JPanel vatActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        vatActions.add(extraVatStatusLabel);
        vatActions.add(extraVatButton);
        vatPanel.add(vatActions, BorderLayout.SOUTH);

        // Amélioration 2 : embouteillage amélioré
        JPanel bottlingPanel = new JPanel(new BorderLayout(10, 10));
        bottlingPanel.setBorder(BorderFactory.createTitledBorder("Embouteillage amélioré"));

        bottlingPanel.add(
                new JLabel("Réduit les pertes d'embouteillage de 5 % à 2 %. Prix : 1 500 €"),
                BorderLayout.CENTER
        );

        improvedBottlingStatusLabel = new JLabel();
        improvedBottlingButton = new JButton("Acheter - 1 500 €");
        improvedBottlingButton.addActionListener(e -> buyUpgrade("bottling"));

        JPanel bottlingActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottlingActions.add(improvedBottlingStatusLabel);
        bottlingActions.add(improvedBottlingButton);
        bottlingPanel.add(bottlingActions, BorderLayout.SOUTH);

        // Amélioration 3 : contrôle qualité
        JPanel qualityPanel = new JPanel(new BorderLayout(10, 10));
        qualityPanel.setBorder(BorderFactory.createTitledBorder("Contrôle qualité"));

        qualityPanel.add(
                new JLabel("Augmente la réputation de 10 points. Prix : 1 000 €"),
                BorderLayout.CENTER
        );

        qualityControlStatusLabel = new JLabel();
        qualityControlButton = new JButton("Acheter - 1 000 €");
        qualityControlButton.addActionListener(e -> buyUpgrade("quality"));

        JPanel qualityActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        qualityActions.add(qualityControlStatusLabel);
        qualityActions.add(qualityControlButton);
        qualityPanel.add(qualityActions, BorderLayout.SOUTH);

        panel.add(vatPanel);
        panel.add(bottlingPanel);
        panel.add(qualityPanel);

        refreshUpgradePanel();

        return panel;
    }

    private void refreshUpgradePanel() {

        boolean vatPurchased = company.hasExtraVat();
        boolean bottlingPurchased = company.hasImprovedBottling();
        boolean qualityPurchased = company.hasQualityControl();

        extraVatStatusLabel.setText(vatPurchased ? "Achetée" : "Non achetée");
        improvedBottlingStatusLabel.setText(bottlingPurchased ? "Achetée" : "Non achetée");
        qualityControlStatusLabel.setText(qualityPurchased ? "Achetée" : "Non achetée");

        extraVatButton.setEnabled(!vatPurchased);
        improvedBottlingButton.setEnabled(!bottlingPurchased);
        qualityControlButton.setEnabled(!qualityPurchased);

        extraVatButton.setText(vatPurchased ? "Déjà achetée" : "Acheter - 2 000 €");
        improvedBottlingButton.setText(bottlingPurchased ? "Déjà achetée" : "Acheter - 1 500 €");
        qualityControlButton.setText(qualityPurchased ? "Déjà achetée" : "Acheter - 1 000 €");
    }

    private void buyUpgrade(String upgrade) {

        float price;
        String description;

        // Déterminer le prix et la description
        switch (upgrade) {
            case "vat":
                price = 2000f;
                description = "Achat d'une cuve supplémentaire";
                break;

            case "bottling":
                price = 1500f;
                description = "Achat de l'amélioration d'embouteillage";
                break;

            case "quality":
                price = 1000f;
                description = "Achat du contrôle qualité";
                break;

            default:
                return;
        }

        // Vérifier que l'amélioration n'a pas déjà été achetée
        boolean alreadyPurchased =
                (upgrade.equals("vat") && company.hasExtraVat())
                        || (upgrade.equals("bottling") && company.hasImprovedBottling())
                        || (upgrade.equals("quality") && company.hasQualityControl());

        if (alreadyPurchased) {
            JOptionPane.showMessageDialog(this, "Cette amélioration est déjà achetée.");
            return;
        }

        // Vérifier la trésorerie
        if (company.getMoney() < price) {
            JOptionPane.showMessageDialog(
                    this,
                    String.format(
                            Locale.FRANCE,
                            "Fonds insuffisants !\nPrix : %.2f €\nTrésorerie : %.2f €",
                            price,
                            company.getMoney()
                    ),
                    "Achat impossible",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Demander confirmation
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                String.format(
                        Locale.FRANCE,
                        "Confirmer l'achat de cette amélioration pour %.2f € ?",
                        price
                ),
                "Confirmer l'achat",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        // Retirer l'argent une seule fois
        company.setMoney(company.getMoney() - price);

        // Enregistrer la dépense dans les finances
        financeService.recordExpense(company, price, description);

        // Appliquer l'amélioration
        switch (upgrade) {
            case "vat":
                company.setExtraVatPurchased(true);
                break;

            case "bottling":
                company.setImprovedBottlingPurchased(true);
                break;

            case "quality":
                company.setQualityControlPurchased(true);
                company.setReputation(company.getReputation() + 10);
                break;
        }

        JOptionPane.showMessageDialog(
                this,
                "Amélioration achetée avec succès !"
        );

        // Actualiser les panneaux concernés
        refreshUpgradePanel();
        refreshDashboard();
        refreshProduction();
        refreshFinancePanel();
    }

    // Créer temporairement un panneau vide
    private JPanel createPlaceholder(String panelName) {

        JPanel panel = new JPanel(new BorderLayout());

        JLabel message = new JLabel(
                panelName + " : panneau à développer",
                SwingConstants.CENTER
        );

        message.setFont(new Font("Arial", Font.PLAIN, 20));

        panel.add(message, BorderLayout.CENTER);

        return panel;
    }

    // Actualiser les informations du tableau de bord
    private void refreshDashboard() {

        saveNameLabel.setText(currentSaveName);

        dayLabel.setText("Jour " + company.getCurrentDay());

        moneyLabel.setText(
                String.format(
                        Locale.FRANCE,
                        "%.2f €",
                        company.getMoney()
                )
        );

        reputationLabel.setText(
                company.getReputation() + " / 100"
        );

        if (company.isBankrupt()) {
            alertLabel.setText(
                    "Attention : votre entreprise est en faillite."
            );
            alertLabel.setForeground(new Color(145, 55, 48));
        } else {
            alertLabel.setText(
                    "Votre brasserie est en activité."
            );
            alertLabel.setForeground(CraftTheme.TEXT);
        }
    }

    private JPanel createSalesPanel() {

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Ventes et commandes clients");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        bottleStockTable = new JTable(bottleStockTableModel);
        bottleStockTable.setRowHeight(24);

        JPanel topPanel = new JPanel(new BorderLayout(5, 10));
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(new JLabel("Stock de bouteilles"), BorderLayout.CENTER);
        topPanel.add(new JScrollPane(bottleStockTable), BorderLayout.SOUTH);

        String[] columns = {
                "Client", "Bière", "Quantité",
                "Prix / bouteille", "Montant total",
                "Échéance", "Statut"
        };

        ordersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ordersTable = new JTable(ordersTableModel);
        ordersTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane = new JScrollPane(ordersTable);

        JButton acceptButton = new JButton("Accepter");
        JButton refuseButton = new JButton("Refuser");
        JButton refreshButton = new JButton("Actualiser");

        acceptButton.addActionListener(e -> acceptSelectedOrder());
        refuseButton.addActionListener(e -> refuseSelectedOrder());
        refreshButton.addActionListener(e -> refreshOrders());

        JPanel buttonsPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        buttonsPanel.add(acceptButton);
        buttonsPanel.add(refuseButton);
        buttonsPanel.add(refreshButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        refreshOrders();

        return panel;
    }

    private void refreshBottleStock() {
        if (bottleStockTableModel == null) {
            return;
        }

        bottleStockTableModel.setRowCount(0);

        String[] styles = {"Blonde", "IPA", "Rousse", "Brune"};

        for (String style : styles) {
            bottleStockTableModel.addRow(new Object[]{
                    style,
                    company.getBottleStock(style)
            });
        }
    }

    private void refreshOrders() {

        ordersTableModel.setRowCount(0);

        for (Order order : company.getOrders()) {

            ordersTableModel.addRow(new Object[] {
                    order.getCustomer().getName(),
                    order.getStyle(),
                    order.getQuantity(),
                    String.format(
                            java.util.Locale.FRANCE,
                            "%.2f €",
                            order.getPricePerBottle()
                    ),
                    String.format(
                            java.util.Locale.FRANCE,
                            "%.2f €",
                            order.getTotalPrice()
                    ),
                    "Jour " + order.getDeadlineDay(),
                    order.getStatus()
            });
        }

        refreshBottleStock();
    }

    private Order getSelectedOrder() {

        int selectedRow = ordersTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Sélectionne d'abord une commande."
            );
            return null;
        }

        // Le tableau affiche les commandes dans le même ordre
        // que la liste de l'entreprise.
        return company.getOrders().get(
                ordersTable.convertRowIndexToModel(selectedRow)
        );
    }

    private void acceptSelectedOrder() {

        Order order = getSelectedOrder();

        if (order == null) {
            return;
        }

        boolean success = salesService.acceptOrder(company, order);

        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Commande acceptée !\n"
                            + "Revenu : "
                            + String.format(
                            java.util.Locale.FRANCE,
                            "%.2f €",
                            order.getTotalPrice()
                    )
            );
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "La commande n'a pas pu être acceptée.\n"
                            + "Vérifie le stock, le statut et l'échéance."
            );
        }

        refreshOrders();
        refreshDashboard();
        refreshStock();
        refreshBottleStock();
    }

    private void refuseSelectedOrder() {

        Order order = getSelectedOrder();

        if (order == null) {
            return;
        }

        boolean success = salesService.refuseOrder(company, order);

        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Commande refusée."
            );
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Cette commande ne peut pas être refusée."
            );
        }

        refreshOrders();
    }


    private JPanel createFinancePanel() {

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel title = new JLabel("Gestion financière");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        // Résumé financier
        JPanel summaryPanel = new JPanel(new GridLayout(3, 2, 15, 10));

        financeMoneyLabel = new JLabel();
        financeRevenueLabel = new JLabel();
        financeExpenseLabel = new JLabel();
        financeProfitLabel = new JLabel();

        Font labelFont = new Font("Arial", Font.BOLD, 16);

        financeMoneyLabel.setFont(labelFont);
        financeRevenueLabel.setFont(labelFont);
        financeExpenseLabel.setFont(labelFont);
        financeProfitLabel.setFont(labelFont);

        summaryPanel.add(financeMoneyLabel);
        summaryPanel.add(financeRevenueLabel);
        summaryPanel.add(financeExpenseLabel);
        summaryPanel.add(financeProfitLabel);

        // Bouton pour déclarer la faillite
        JButton bankruptcyButton = new JButton("Déposer le bilan");

        bankruptcyButton.addActionListener(e -> {

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Attention : déposer le bilan mettra fin à la gestion de cette entreprise.\n"
                            + "Vous pourrez toujours consulter les finances et charger une ancienne sauvegarde.\n\n"
                            + "Voulez-vous vraiment continuer ?",
                    "Déposer le bilan",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {

                company.declareBankruptcy();

                // On enregistre tout de suite la faillite dans la sauvegarde
                // ouverte, sinon elle est perdue au rechargement.
                boolean saved = false;

                if (currentSaveName != null
                        && !currentSaveName.equals("Aucune sauvegarde")) {
                    saved = saveService.saveCompany(
                            company,
                            financeService,
                            "saves/" + currentSaveName
                    );
                }

                refreshDashboard();
                updateTabsAfterBankruptcy();

                JOptionPane.showMessageDialog(
                        this,
                        saved
                                ? "Votre entreprise a fait faillite (sauvegarde mise à jour)."
                                : "Votre entreprise a fait faillite.\n"
                                + "Pensez à la sauvegarder depuis l'onglet Sauvegardes."
                );
            }
        });

        summaryPanel.add(bankruptcyButton);

        // Partie supérieure
        JPanel topPanel = new JPanel(new BorderLayout(10, 15));
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(summaryPanel, BorderLayout.CENTER);

        // Tableau des transactions
        String[] columns = {
                "Jour", "Type", "Montant", "Description"
        };

        financeTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        financeTable = new JTable(financeTableModel);
        financeTable.setRowHeight(24);

        JScrollPane scrollPane = new JScrollPane(financeTable);

        // Ajout des éléments à la fenêtre
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        refreshFinancePanel();

        return panel;
    }

    private void refreshFinancePanel() {

        // Actualiser les informations financières
        financeMoneyLabel.setText(
                String.format(
                        Locale.FRANCE,
                        "Trésorerie : %.2f €",
                        company.getMoney()
                )
        );

        financeRevenueLabel.setText(
                String.format(
                        Locale.FRANCE,
                        "Revenus : %.2f €",
                        financeService.getTotalRevenue()
                )
        );

        financeExpenseLabel.setText(
                String.format(
                        Locale.FRANCE,
                        "Dépenses : %.2f €",
                        financeService.getTotalExpenses()
                )
        );

        financeProfitLabel.setText(
                String.format(
                        Locale.FRANCE,
                        "Bénéfice estimé : %.2f €",
                        financeService.getEstimatedProfit()
                )
        );

        // Actualiser le tableau des transactions
        financeTableModel.setRowCount(0);

        for (FinanceRecord record : financeService.getRecords()) {

            String type = record.getType() == FinanceRecord.Type.REVENUE
                    ? "Revenu"
                    : "Dépense";

            String amount = String.format(
                    Locale.FRANCE,
                    "%.2f €",
                    record.getAmount()
            );

            financeTableModel.addRow(new Object[] {
                    "Jour " + record.getDay(),
                    type,
                    amount,
                    record.getDescription()
            });
        }
    }

    private JPanel createSavePanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Gestion des sauvegardes");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        saveListModel = new DefaultListModel<>();
        saveList = new JList<>(saveListModel);

        JButton saveButton = new JButton("Sauvegarder la partie");
        JButton loadButton = new JButton("Charger la sauvegarde");
        JButton deleteButton = new JButton("Supprimer la sauvegarde");
        JButton newGameButton = new JButton("Nouvelle partie");

        // Créer une nouvelle partie
        newGameButton.addActionListener(e -> createNewGame());

        // Supprimer une sauvegarde
        deleteButton.addActionListener(e -> {

            File selectedFile = saveList.getSelectedValue();

            if (selectedFile == null) {
                JOptionPane.showMessageDialog(this, "Sélectionne une sauvegarde.");
                return;
            }

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Veux-tu vraiment supprimer la sauvegarde "
                            + selectedFile.getName() + " ?",
                    "Supprimer une sauvegarde",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {

                if (saveService.deleteSave(selectedFile)) {

                    // Si on supprime la partie actuellement ouverte
                    if (selectedFile.getName().equals(currentSaveName)) {
                        currentSaveName = "Aucune sauvegarde";
                        refreshDashboard();
                    }

                    JOptionPane.showMessageDialog(this, "Sauvegarde supprimée !");
                    refreshSaveList();

                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Impossible de supprimer cette sauvegarde."
                    );
                }
            }
        });

        // Sauvegarder la partie actuelle
        saveButton.addActionListener(e -> {

            String fileName = currentSaveName;

            // Si aucune sauvegarde n'est actuellement ouverte
            if (fileName == null || fileName.equals("Aucune sauvegarde")) {

                String name = JOptionPane.showInputDialog(
                        this,
                        "Nom de votre nouvelle sauvegarde :"
                );

                if (name == null || name.trim().isEmpty()) {
                    return;
                }

                fileName = name.trim().replaceAll("[^a-zA-Z0-9_-]", "_");

                if (!fileName.endsWith(".json")) {
                    fileName += ".json";
                }
            }

            // Enregistrer dans le fichier correspondant à la partie actuelle
            boolean success = saveService.saveCompany(
                    company,
                    financeService,
                    "saves/" + fileName
            );

            if (success) {

                currentSaveName = fileName;

                refreshDashboard();
                refreshSaveList();

                JOptionPane.showMessageDialog(
                        this,
                        "Partie sauvegardée avec succès !"
                );

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Erreur lors de la sauvegarde."
                );
            }
        });

        // Charger une sauvegarde
        loadButton.addActionListener(e -> loadSelectedSave());

        // Organisation des boutons
        JPanel buttons = new JPanel();
        buttons.add(saveButton);
        buttons.add(loadButton);
        buttons.add(deleteButton);
        buttons.add(newGameButton);

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(saveList), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        refreshSaveList();

        return panel;
    }

    private void refreshSaveList() {
        saveListModel.clear();

        for (File file : saveService.listSaves()) {
            saveListModel.addElement(file);
        }
    }

    private void loadSelectedSave() {
        File selectedFile = saveList.getSelectedValue();

        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Sélectionne une sauvegarde.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Charger cette sauvegarde ? La partie actuelle sera remplacée.",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        Company loadedCompany = saveService.loadCompany(selectedFile.getPath());

        if (loadedCompany == null) {
            JOptionPane.showMessageDialog(this, "Impossible de charger cette sauvegarde.");
            return;
        }

        company = loadedCompany;
        currentSaveName = selectedFile.getName();
        refreshIngredientComboBox();

        financeService = new FinanceService();
        financeService.restoreRecords(
                saveService.loadFinanceRecords(selectedFile.getPath())
        );

        dayService = new DayService(financeService);
        purchaseService = new PurchaseService(financeService);
        salesService = new SalesService(financeService);

        refreshDashboard();
        refreshFinancePanel();
        refreshStock();
        refreshBottling();
        refreshOrders();
        refreshUpgradePanel();

        System.out.println("Entreprise en faillite : " + company.isBankrupt());
        updateTabsAfterBankruptcy();

        JOptionPane.showMessageDialog(this, "Sauvegarde chargée !");
    }

    private void updateTabsAfterBankruptcy() {
        tabs.setEnabledAt(1, !company.isBankrupt()); // Production
        tabs.setEnabledAt(2, !company.isBankrupt()); // Stocks et achats
        tabs.setEnabledAt(3, !company.isBankrupt()); // Embouteillage
        tabs.setEnabledAt(4, !company.isBankrupt()); // Ventes / commandes
        tabs.setEnabledAt(6, !company.isBankrupt()); // Améliorations

        // Ces onglets restent accessibles :
        tabs.setEnabledAt(0, true); // Tableau de bord
        tabs.setEnabledAt(5, true); // Finances
        tabs.setEnabledAt(7, true); // Sauvegardes
    }

    private void createNewGame() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Créer une nouvelle partie ?\n",
                "Nouvelle partie",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String name = JOptionPane.showInputDialog(
                this,
                "Nom de votre nouvelle brasserie :",
                "Nouvelle brasserie"
        );

        if (name == null || name.trim().isEmpty()) {
            return;
        }

        name = name.trim();

        // Création de la nouvelle entreprise
        company = new Company(name);

        // Réinitialisation des services
        financeService = new FinanceService();
        dayService = new DayService(financeService);
        purchaseService = new PurchaseService(financeService);
        salesService = new SalesService(financeService);

        // Création du nom de fichier
        String fileName = name.replaceAll("[^a-zA-Z0-9_-]", "_");
        if (!fileName.endsWith(".json")) {
            fileName += ".json";
        }

        String filePath = "saves/" + fileName;

        // Sauvegarde immédiate de la nouvelle partie
        boolean success = saveService.saveCompany(
                company,
                financeService,
                filePath
        );

        if (!success) {
            JOptionPane.showMessageDialog(
                    this,
                    "La nouvelle partie a été créée, mais sa sauvegarde a échoué."
            );
        } else {
            currentSaveName = fileName;
        }

        // Actualisation de l'interface
        refreshIngredientComboBox();
        refreshDashboard();
        refreshStock();
        refreshProduction();
        refreshBottling();
        refreshOrders();
        refreshFinancePanel();
        refreshUpgradePanel();
        refreshSaveList();

        updateTabsAfterBankruptcy();

        JOptionPane.showMessageDialog(
                this,
                success
                        ? "Nouvelle partie créée et sauvegardée !"
                        : "Nouvelle partie créée, mais non sauvegardée."
        );
    }
}