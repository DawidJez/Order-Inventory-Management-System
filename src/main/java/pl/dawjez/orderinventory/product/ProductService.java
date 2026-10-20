package pl.dawjez.orderinventory.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dawjez.orderinventory.common.DuplicateSkuException;
import pl.dawjez.orderinventory.common.ResourceNotFoundException;
import pl.dawjez.orderinventory.inventory.StockService;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final StockService stockService;

    // Creates a product together with its stock row, in one transaction.
    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateSkuException(request.sku());
        }

        Product product = productRepository.save(
                new Product(request.sku(), request.name(), request.description(), request.price()));

        stockService.createInitialStock(product, request.initialQuantity());

        return ProductResponse.from(product);
    }

    public ProductResponse get(Long id) {
        return ProductResponse.from(findOrThrow(id));
    }


    public List<ProductResponse> list() {
        return productRepository.findAll().stream().map(ProductResponse::from).toList();
    }


    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = findOrThrow(id);
        product.updateDetails(request.name(), request.description(), request.price());
        return ProductResponse.from(product);
    }

    // orElseThrow turns "empty" into a domain exception
    // that GlobalExceptionHandler maps to 404.
    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product %d not found".formatted(id)));
    }
}
