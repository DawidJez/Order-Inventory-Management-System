package pl.dawjez.orderinventory.inventory;


public record StockResponse(
        Long productId,
        int quantityOnHand,
        int quantityReserved,
        int quantityAvailable
) {

    public static StockResponse from(Stock stock) {
        return new StockResponse(
                stock.getProduct().getId(),
                stock.getQuantityOnHand(),
                stock.getQuantityReserved(),
                stock.getQuantityAvailable()
        );
    }
}
