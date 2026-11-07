package brasserie.model;

import java.util.List;

public class RegularCustomer extends Customer {

    public RegularCustomer(String name, List<String> preferredStyles,
                           int minOrderQuantity, int maxOrderQuantity,
                           float targetPrice) {
        super(name, preferredStyles, minOrderQuantity, maxOrderQuantity, targetPrice);
    }

    @Override
    public float calculateOfferedPrice(float variation) {
        return Math.max(0.01f, getTargetPrice() + variation);
    }

    @Override
    public String getCustomerType() {
        return "REGULAR";
    }
}