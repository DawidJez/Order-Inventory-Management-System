package pl.dawjez.orderinventory.order;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long productId,
        String sku,
        String name,
        int quantity,
        BigDecimal priceAtPurchase,
        BigDecimal subtotal
) {

    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getProduct().getId(),
                item.getProduct().getSku(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPriceAtPurchase(),
                item.getSubtotal()
        );
    }
}
