package brasserie.model;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String style, int available, int requested) {
        super("Stock insuffisant pour " + style
                + ". Disponible : " + available
                + ", demandé : " + requested);
    }
}