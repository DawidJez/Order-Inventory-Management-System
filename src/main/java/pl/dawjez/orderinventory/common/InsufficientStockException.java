package pl.dawjez.orderinventory.common;

/**
 * Thrown when an order asks for more units than are currently available.
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(Long productId, int requested, int available) {
        super("Product %d: requested %d units, only %d available".formatted(productId, requested, available));
    }
}
