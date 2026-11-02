package pl.dawjez.orderinventory.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * There is no price here on purpose: the price is always read
 * from the database, so a client can never choose what it pays.
 */
public record OrderItemRequest(

        @NotNull
        Long productId,

        @Min(1)
        int quantity
) {
}
