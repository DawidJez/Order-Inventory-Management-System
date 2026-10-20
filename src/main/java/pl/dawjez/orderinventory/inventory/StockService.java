package pl.dawjez.orderinventory.inventory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dawjez.orderinventory.common.ResourceNotFoundException;
import pl.dawjez.orderinventory.product.Product;


// Business logic for warehouse stock.
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final StockRepository stockRepository;

    /**
     * It is called from ProductService.create, so it joins that transaction (the default
     * propagation is REQUIRED) and the stock row is rolled back together with the product if
     * anything fails. @Transactional is repeated here to override the class-level readOnly
     * setting, because this method writes.
     */
    @Transactional
    public Stock createInitialStock(Product product, int initialQuantity) {
        return stockRepository.save(new Stock(product, initialQuantity));
    }

    public StockResponse get(Long productId) {
        return StockResponse.from(findOrThrow(productId));
    }

    /**
     * Adds units to the warehouse.
     *
     * The change is made through Stock.restock, which validates the quantity, and saved by dirty
     * checking at commit. @Version on Stock protects against two concurrent restocks overwriting
     * each other: the second one fails instead of silently losing an update.
     */
    @Transactional
    public StockResponse restock(Long productId, int quantity) {
        Stock stock = findOrThrow(productId);
        stock.restock(quantity);
        return StockResponse.from(stock);
    }

    private Stock findOrThrow(Long productId) {
        return stockRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Stock for product %d not found".formatted(productId)));
    }
}
