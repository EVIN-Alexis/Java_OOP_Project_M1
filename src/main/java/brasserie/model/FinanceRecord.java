package brasserie.model;

public class FinanceRecord {

    public enum Type {
        EXPENSE,
        REVENUE
    }

    private int day;
    private Type type;
    private float amount;
    private String description;

    public FinanceRecord(int day, Type type, float amount,
                         String description) {
        this.day = day;
        this.type = type;
        this.amount = Math.round(amount * 100f) / 100f;
        this.description = description;
    }

    public int getDay() {
        return day;
    }

    public Type getType() {
        return type;
    }

    public float getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}