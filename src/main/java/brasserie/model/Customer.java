package brasserie.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Customer {

    private String name;
    private List<String> preferredStyles;
    private int minOrderQuantity;
    private int maxOrderQuantity;
    private float targetPrice;

    public Customer(String name, List<String> preferredStyles,
                    int minOrderQuantity, int maxOrderQuantity,
                    float targetPrice) {
        this.name = name;
        this.preferredStyles = new ArrayList<>(preferredStyles);
        this.minOrderQuantity = minOrderQuantity;
        this.maxOrderQuantity = maxOrderQuantity;
        this.targetPrice = targetPrice;
    }

    public String getName() {
        return name;
    }

    public List<String> getPreferredStyles() {
        return new ArrayList<>(preferredStyles);
    }

    public int getMinOrderQuantity() {
        return minOrderQuantity;
    }

    public int getMaxOrderQuantity() {
        return maxOrderQuantity;
    }

    public float getTargetPrice() {
        return targetPrice;
    }

    public boolean likesStyle(String style) {
        return preferredStyles.contains(style);
    }

    public abstract float calculateOfferedPrice(float variation);

    public abstract String getCustomerType();
}