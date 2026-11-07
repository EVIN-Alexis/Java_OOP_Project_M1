package brasserie.model;

public class Vat {

    private int id;
    private float capacityLiters;
    private BrewBatch currentBatch;

    public Vat(int id, float capacityLiters) {
        this.id = id;
        this.capacityLiters = capacityLiters;
        this.currentBatch = null;
    }

    public int getId() {
        return id;
    }

    public float getCapacityLiters() {
        return capacityLiters;
    }

    public BrewBatch getCurrentBatch() {
        return currentBatch;
    }

    public boolean isOccupied() {
        return currentBatch != null;
    }

    public void setCurrentBatch(BrewBatch batch) {
        this.currentBatch = batch;
    }

    public void empty() {
        this.currentBatch = null;
    }
}