package brasserie.model;

import java.util.List;

public class ProfessionalCustomer extends Customer implements Discountable {

    private static final float DISCOUNT = 0.10f;

    public ProfessionalCustomer(String name, List<String> preferredStyles,
                                int minOrderQuantity, int maxOrderQuantity,
                                float targetPrice) {
        super(name, preferredStyles, minOrderQuantity,
                maxOrderQuantity, targetPrice);
    }

    @Override
    public float applyDiscount(float price) {
        return price * (1 - DISCOUNT);
    }

    @Override
    public float calculateOfferedPrice(float variation) {
        return Math.max(0.01f,
                applyDiscount(getTargetPrice()) + variation);
    }

    @Override
    public String getCustomerType() {
        return "PROFESSIONAL";
    }
}