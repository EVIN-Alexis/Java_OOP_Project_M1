package brasserie.model;

public class Recipe {

    private String style;
    private String commercialName;

    private float standardMaltKg;
    private float specialMaltKg;
    private float standardHopsKg;
    private float premiumHopsKg;
    private int yeastSachets;

    private float volumeLiters;

    public Recipe(String style, String commercialName,
                  float standardMaltKg, float specialMaltKg,
                  float standardHopsKg, float premiumHopsKg,
                  int yeastSachets, float volumeLiters) {
        this.style = style;
        this.commercialName = commercialName;
        this.standardMaltKg = standardMaltKg;
        this.specialMaltKg = specialMaltKg;
        this.standardHopsKg = standardHopsKg;
        this.premiumHopsKg = premiumHopsKg;
        this.yeastSachets = yeastSachets;
        this.volumeLiters = volumeLiters;
    }

    public String getStyle() {
        return style;
    }

    public String getCommercialName() {
        return commercialName;
    }

    public void setCommercialName(String commercialName) {
        this.commercialName = commercialName;
    }

    public float getStandardMaltKg() {
        return standardMaltKg;
    }

    public float getSpecialMaltKg() {
        return specialMaltKg;
    }

    public float getStandardHopsKg() {
        return standardHopsKg;
    }

    public float getPremiumHopsKg() {
        return premiumHopsKg;
    }

    public int getYeastSachets() {
        return yeastSachets;
    }

    public float getVolumeLiters() {
        return volumeLiters;
    }

    @Override
    public String toString() {
        return commercialName + " (" + style + ")";
    }
}