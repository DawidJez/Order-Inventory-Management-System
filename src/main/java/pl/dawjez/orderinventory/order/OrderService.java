package pl.dawjez.orderinventory.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dawjez.orderinventory.common.InsufficientStockException;
import pl.dawjez.orderinventory.common.InvalidOrderRequestException;
import pl.dawjez.orderinventory.common.InvalidOrderStateException;
import pl.dawjez.orderinventory.common.ResourceNotFoundException;
import pl.dawjez.orderinventory.inventory.Stock;
import pl.dawjez.orderinventory.inventory.StockRepository;
import pl.dawjez.orderinventory.product.Product;
import pl.dawjez.orderinventory.product.ProductRepository;
import pl.dawjez.orderinventory.user.User;
import pl.dawjez.orderinventory.user.UserRepository;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Placing, reading and cancelling orders.
 * Reads are read-only transactions by default. Methods that change data override that with a
 * plain @Transactional, so everything inside one method either commits together or rolls back together.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;

    /**
     * Places an order and reserves stock for every line.
     *
     * Everything runs in one transaction: if the third product has no stock, the exception rolls
     * back the reservations already made for the first two, so a half-reserved order can never exist.
     */
    @Transactional
    public OrderResponse placeOrder(Long userId, PlaceOrderRequest request) {
        List<OrderItemRequest> lines = request.items().stream()
                .sorted(Comparator.comparing(OrderItemRequest::productId))
                .toList();

        Set<Long> seen = new HashSet<>();
        for (OrderItemRequest line : lines) {
            if (!seen.add(line.productId())) {
                throw new InvalidOrderRequestException(
                        "Product %d appears more than once in the order".formatted(line.productId()));
            }
        }

        Map<Long, Product> products = productRepository.findAllById(seen).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        // getReferenceById returns a lightweight proxy without a SELECT: we only need its id for the foreign key.
        User customer = userRepository.getReferenceById(userId);
        Order order = new Order(customer);

        for (OrderItemRequest line : lines) {
            Product product = products.get(line.productId());

            if (product == null) {
                throw new ResourceNotFoundException("Product %d not found".formatted(line.productId()));
            }
            Stock stock = findStock(product.getId());

            if (line.quantity() > stock.getQuantityAvailable()) {
                throw new InsufficientStockException(product.getId(), line.quantity(), stock.getQuantityAvailable());
            }
            stock.reserve(line.quantity());
            order.addItem(product, line.quantity());
        }

        return OrderResponse.from(orderRepository.save(order));
    }

    /**
     * Returns the caller's own orders, or every order for an ADMIN.
     */
    public List<OrderResponse> listOrders(Long userId, boolean admin) {
        List<Order> orders = admin
                ? orderRepository.findAllByOrderByCreatedAtDesc()
                : orderRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream().map(OrderResponse::from).toList();
    }

    public OrderResponse getOrder(Long orderId, Long userId, boolean admin) {
        return OrderResponse.from(loadAccessibleOrder(orderId, userId, admin));
    }

    /**
     * Cancels an order and gives the reserved stock back.
     *
     * The status rules live in OrderStatus.canTransitionTo, so a completed or already cancelled
     * order is rejected with 409. Stock and order are modified inside the same transaction, which
     * keeps them consistent.
     */
    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long userId, boolean admin) {
        Order order = loadAccessibleOrder(orderId, userId, admin);

        if (!order.getStatus().canTransitionTo(OrderStatus.CANCELLED)) {
            throw new InvalidOrderStateException(
                    "Order %d is %s and cannot be cancelled".formatted(orderId, order.getStatus()));
        }

        for (OrderItem item : order.getItems()) {
            findStock(item.getProduct().getId()).release(item.getQuantity());
        }
        order.changeStatus(OrderStatus.CANCELLED, userRepository.getReferenceById(userId));
        return OrderResponse.from(order);
    }

    /**
     * Loads an order the caller is allowed to see. Someone else's order is reported as "not found"
     * instead of "forbidden", so the API does not reveal which order ids exist.
     */
    private Order loadAccessibleOrder(Long orderId, Long userId, boolean admin) {
        return orderRepository.findById(orderId)
                .filter(order -> admin || order.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Order %d not found".formatted(orderId)));
    }

    private Stock findStock(Long productId) {
        return stockRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock for product %d not found".formatted(productId)));
    }
}
