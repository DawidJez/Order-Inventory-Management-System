package pl.dawjez.orderinventory.inventory;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/products/{productId}/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public StockResponse get(@PathVariable Long productId) {
        return stockService.get(productId);
    }

    // A POST to an action path as restocking operation
    @PostMapping("/restock")
    public StockResponse restock(@PathVariable Long productId, @Valid @RequestBody RestockRequest request) {
        return stockService.restock(productId, request.quantity());
    }
}
