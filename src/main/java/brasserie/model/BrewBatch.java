package brasserie.model;

public class BrewBatch {
    private Recipe recipe;
    private float volumeLiters;
    private int startDay;
    private Vat vat;
    private boolean bottled;

    public BrewBatch(Recipe recipe, int startDay, Vat vat) {
        this.recipe = recipe;
        this.volumeLiters = recipe.getVolumeLiters();
        this.startDay = startDay;
        this.vat = vat;
        this.bottled = false;
    }

    // Constructeur conservé pour rester compatible
    // avec les anciens tests qui ne précisent pas de cuve.
    public BrewBatch(Recipe recipe, int startDay) {
        this(recipe, startDay, null);
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public float getVolumeLiters() {
        return volumeLiters;
    }

    public int getStartDay() {
        return startDay;
    }

    public Vat getVat() {
        return vat;
    }

    public boolean isFermentationComplete(int currentDay) {
        return currentDay - startDay >= 3;
    }

    public boolean isBottled() {
        return bottled;
    }

    public void setBottled(boolean bottled) {
        this.bottled = bottled;
    }
}