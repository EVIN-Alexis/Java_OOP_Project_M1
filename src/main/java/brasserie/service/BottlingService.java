package brasserie.service;

import brasserie.model.BrewBatch;
import brasserie.model.Company;

public class BottlingService {

    private static final float BOTTLE_VOLUME = 0.33f;

    public int bottleBatch(Company company, BrewBatch batch) {

        if (company == null || batch == null) {
            System.out.println("Erreur : entreprise ou brassin invalide.");
            return 0;
        }

        if (company.isBankrupt()) {
            System.out.println("Impossible : l'entreprise est en faillite.");
            return 0;
        }

        if (!company.getBatches().contains(batch)) {
            System.out.println("Erreur : ce brassin n'appartient pas à l'entreprise.");
            return 0;
        }

        if (batch.isBottled()) {
            System.out.println("Ce brassin a déjà été embouteillé.");
            return 0;
        }

        if (!batch.isFermentationComplete(company.getCurrentDay())) {
            System.out.println("Le brassin n'a pas terminé sa fermentation.");
            return 0;
        }

        float lossRate = company.hasImprovedBottling() ? 0.02f : 0.05f;

        float usableVolume = batch.getVolumeLiters() * (1 - lossRate);

        int bottleCount =
                (int) (usableVolume / BOTTLE_VOLUME);

        if (bottleCount <= 0) {
            System.out.println("Volume insuffisant pour embouteiller.");
            return 0;
        }

        String style = batch.getRecipe().getStyle();

        company.addBottles(style, bottleCount);
        batch.setBottled(true);

        // La cuve est libérée après l'embouteillage.
        if (batch.getVat() != null
                && batch.getVat().getCurrentBatch() == batch) {
            batch.getVat().empty();
        }

        System.out.println("=== EMBOUTEILLAGE TERMINÉ ===");
        System.out.println("Bière : " + style);
        System.out.println("Volume utilisable : " + usableVolume + " L");
        System.out.println("Bouteilles produites : " + bottleCount);
        System.out.println("Stock disponible : "
                + company.getBottleStock(style));

        return bottleCount;
    }
}