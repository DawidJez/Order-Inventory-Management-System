package pl.dawjez.orderinventory.inventory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.dawjez.orderinventory.product.Product;

@Entity
@Table(name = "stocks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(name = "quantity_on_hand", nullable = false)
    private int quantityOnHand;

    @Column(name = "quantity_reserved", nullable = false)
    private int quantityReserved;

    // a thingy that will help later with locks
    @Version
    private Long version;

    public Stock(Product product, int initialQuantity) {
        this.product = product;
        this.quantityOnHand = initialQuantity;
        this.quantityReserved = 0;
    }

    public int getQuantityAvailable() {
        return quantityOnHand - quantityReserved;
    }

    public void reserve(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Reserved quantity must be positive");
        }
        if (quantity > getQuantityAvailable()) {
            throw new IllegalStateException(
                    "Cannot reserve %d units, only %d available".formatted(quantity, getQuantityAvailable())
            );
        }
        this.quantityReserved += quantity;
    }

    public void release(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Released quantity must be positive");
        }
        if (quantity > this.quantityReserved) {
            throw new IllegalStateException(
                    "Cannot release %d units, only %d reserved".formatted(quantity, this.quantityReserved)
            );
        }
        this.quantityReserved -= quantity;
    }

    public void restock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restocked quantity must be positive");
        }
        this.quantityOnHand += quantity;
    }
}
