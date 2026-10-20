package pl.dawjez.orderinventory.product;

import java.math.BigDecimal;
import java.time.Instant;


public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        Instant createdAt
) {

    // Manual mapping instead of a mapping library: with a handful of fields it is a few lines
    // and every step is visible.
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCreatedAt()
        );
    }
}
