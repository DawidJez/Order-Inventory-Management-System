package pl.dawjez.orderinventory.order;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.dawjez.orderinventory.product.Product;
import pl.dawjez.orderinventory.user.User;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // the customer who placed the order
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<OrderStatusHistory> statusHistory = new ArrayList<>();

    public Order(User user) {
        this.user = user;
        this.status = OrderStatus.CREATED;
        this.statusHistory.add(new OrderStatusHistory(this, null, OrderStatus.CREATED, user));
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public void addItem(Product product, int quantity) {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Items can be added only to an order in CREATED status");
        }
        boolean alreadyPresent = items.stream()
                .anyMatch(item -> item.getProduct().getId().equals(product.getId()));

        if (alreadyPresent) {
            throw new IllegalArgumentException("Product %d is already in this order".formatted(product.getId()));
        }
        items.add(new OrderItem(this, product, quantity, product.getPrice()));
    }

    public void changeStatus(OrderStatus target, User changedBy) {
        if (!status.canTransitionTo(target)) {
            throw new IllegalStateException(
                    "Cannot change order status from %s to %s".formatted(status, target));
        }
        statusHistory.add(new OrderStatusHistory(this, status, target, changedBy));
        this.status = target;
    }

    public BigDecimal getTotalAmount() {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public List<OrderStatusHistory> getStatusHistory() {
        return Collections.unmodifiableList(statusHistory);
    }
}
