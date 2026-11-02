package pl.dawjez.orderinventory.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * What the API returns for an order. It is built inside the service transaction, because the
 * items and products are loaded lazily and need an open session.
 */
public record OrderResponse(
        Long id,
        Long userId,
        OrderStatus status,
        Instant createdAt,
        List<OrderItemResponse> items,
        BigDecimal totalAmount
) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getItems().stream().map(OrderItemResponse::from).toList(),
                order.getTotalAmount()
        );
    }
}
