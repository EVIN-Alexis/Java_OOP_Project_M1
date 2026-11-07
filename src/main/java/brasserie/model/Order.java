package brasserie.model;

public class Order {

    public enum Status {
        PENDING,
        ACCEPTED,
        REFUSED,
        EXPIRED
    }

    private Customer customer;
    private String style;
    private int quantity;
    private float pricePerBottle;
    private int deadlineDay;
    private Status status;
    private int resolutionDay;

    public Order(Customer customer, String style, int quantity,
                 float pricePerBottle, int deadlineDay) {
        this.customer = customer;
        this.style = style;
        this.quantity = quantity;
        this.pricePerBottle = pricePerBottle;
        this.deadlineDay = deadlineDay;
        this.status = Status.PENDING;
        this.resolutionDay = 0;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getStyle() {
        return style;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getPricePerBottle() {
        return pricePerBottle;
    }

    public int getDeadlineDay() {
        return deadlineDay;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public float getTotalPrice() {
        return Math.round(quantity * pricePerBottle * 100f) / 100f;
    }

    public boolean isExpired(int currentDay) {
        return status == Status.PENDING && currentDay > deadlineDay;
    }

    public int getResolutionDay() {
        return resolutionDay;
    }

    public void resolve(Status status, int currentDay) {
        if (status == null || status == Status.PENDING) {
            return;
        }

        this.status = status;
        this.resolutionDay = currentDay;
    }
}