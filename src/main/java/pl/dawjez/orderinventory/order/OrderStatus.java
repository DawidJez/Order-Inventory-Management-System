package pl.dawjez.orderinventory.order;


public enum OrderStatus {
    CREATED,
    CONFIRMED,
    COMPLETED,
    CANCELLED;

    public boolean canTransitionTo(OrderStatus target) {
        return switch (this) {
            case CREATED -> target == CONFIRMED || target == CANCELLED;
            case CONFIRMED -> target == COMPLETED || target == CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
