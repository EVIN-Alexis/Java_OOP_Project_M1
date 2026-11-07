package brasserie.model;

public class Ingredient {
    private String name;
    private String unit;
    private float unitPrice;
    private float quantity;

    public Ingredient(String name, String unit,
                      float unitPrice, float quantity) {
        this.name = name;
        this.unit = unit;
        this.unitPrice = Math.round(unitPrice * 100f) / 100f;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        if (quantity >= 0) {
            this.quantity = quantity;
        }
    }

    @Override
    public String toString() {
        return name + " (" + unit + ")";
    }
}