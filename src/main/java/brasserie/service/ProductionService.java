package brasserie.service;

import brasserie.model.BrewBatch;
import brasserie.model.Company;
import brasserie.model.Ingredient;
import brasserie.model.Recipe;
import brasserie.model.Vat;

public class ProductionService {

    public BrewBatch launchBatch(Company company, Recipe recipe) {

        if (company == null || recipe == null) {
            System.out.println("Erreur : entreprise ou recette invalide.");
            return null;
        }

        if (company.isBankrupt()) {
            System.out.println("Impossible : l'entreprise est en faillite.");
            return null;
        }

        // Chercher une cuve libre et assez grande
        Vat availableVat = null;

        for (Vat vat : company.getVats()) {
            if (!vat.isOccupied()
                    && vat.getCapacityLiters() >= recipe.getVolumeLiters()) {
                availableVat = vat;
                break;
            }
        }

        if (availableVat == null) {
            System.out.println("Lancement refusé : aucune cuve disponible.");
            return null;
        }

        // Retrouver les ingrédients nécessaires
        Ingredient standardMalt =
                findIngredient(company, "Malt standard");
        Ingredient specialMalt =
                findIngredient(company, "Malt spécial");
        Ingredient standardHops =
                findIngredient(company, "Houblon standard");
        Ingredient premiumHops =
                findIngredient(company, "Houblon premium");
        Ingredient standardYeast =
                findIngredient(company, "Levure standard");

        if (standardMalt == null || specialMalt == null
                || standardHops == null || premiumHops == null
                || standardYeast == null) {
            System.out.println("Erreur : un ingrédient est introuvable.");
            return null;
        }

        // Vérifier tous les stocks avant de les modifier
        if (standardMalt.getQuantity() < recipe.getStandardMaltKg()
                || specialMalt.getQuantity() < recipe.getSpecialMaltKg()
                || standardHops.getQuantity() < recipe.getStandardHopsKg()
                || premiumHops.getQuantity() < recipe.getPremiumHopsKg()
                || standardYeast.getQuantity() < recipe.getYeastSachets()) {
            System.out.println("Lancement refusé : ingrédients insuffisants.");
            return null;
        }

        // Consommer les ingrédients
        standardMalt.setQuantity(
                standardMalt.getQuantity() - recipe.getStandardMaltKg());

        specialMalt.setQuantity(
                specialMalt.getQuantity() - recipe.getSpecialMaltKg());

        standardHops.setQuantity(
                standardHops.getQuantity() - recipe.getStandardHopsKg());

        premiumHops.setQuantity(
                premiumHops.getQuantity() - recipe.getPremiumHopsKg());

        standardYeast.setQuantity(
                standardYeast.getQuantity() - recipe.getYeastSachets());

        // Créer le brassin en lui attribuant une cuve
        BrewBatch batch = new BrewBatch(
                recipe,
                company.getCurrentDay(),
                availableVat
        );

        // Réserver la cuve et enregistrer le brassin
        availableVat.setCurrentBatch(batch);
        company.getBatches().add(batch);

        System.out.println("Brassin lancé avec succès !");
        System.out.println("Recette : " + recipe.getStyle());
        System.out.println("Volume prévu : " + batch.getVolumeLiters() + " L");
        System.out.println("Jour de lancement : " + batch.getStartDay());
        System.out.println("Cuve utilisée : " + availableVat.getId());
        System.out.println("Nombre de brassins en cours : "
                + company.getBatches().size());

        return batch;
    }

    private Ingredient findIngredient(
            Company company, String ingredientName) {

        for (Ingredient ingredient : company.getIngredients()) {
            if (ingredient.getName().equals(ingredientName)) {
                return ingredient;
            }
        }

        return null;
    }
}