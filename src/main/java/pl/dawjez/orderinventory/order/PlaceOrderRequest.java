package pl.dawjez.orderinventory.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request body for placing an order. The customer is not part of it: it is taken from the JWT,
 * so nobody can place an order on behalf of someone else.
 */
public record PlaceOrderRequest(

        // @Valid makes Bean Validation also check every element of the list.
        @NotEmpty @Size(max = 50) @Valid
        List<OrderItemRequest> items
) {
}
