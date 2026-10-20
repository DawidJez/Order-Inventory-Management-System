package pl.dawjez.orderinventory.common;

// Thrown when someone tries to create a product with a SKU that is already taken.
public class DuplicateSkuException extends RuntimeException {

    public DuplicateSkuException(String sku) {
        super("Product with SKU '%s' already exists".formatted(sku));
    }
}
