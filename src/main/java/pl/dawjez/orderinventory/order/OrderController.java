package pl.dawjez.orderinventory.order;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // @AuthenticationPrincipal Jwt hands us the verified token. Its subject is the user id (see JwtService).
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@AuthenticationPrincipal Jwt jwt,
                                    @Valid @RequestBody PlaceOrderRequest request) {
        return orderService.placeOrder(userId(jwt), request);
    }

    @GetMapping
    public List<OrderResponse> listOrders(@AuthenticationPrincipal Jwt jwt, Authentication authentication) {
        return orderService.listOrders(userId(jwt), isAdmin(authentication));
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable Long id,
                                  @AuthenticationPrincipal Jwt jwt, Authentication authentication) {
        return orderService.getOrder(id, userId(jwt), isAdmin(authentication));
    }

    // Cancelling changes state, so it is a POST on an action, not a DELETE: the order is kept as history.
    @PostMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable Long id,
                                     @AuthenticationPrincipal Jwt jwt, Authentication authentication) {
        return orderService.cancelOrder(id, userId(jwt), isAdmin(authentication));
    }

    private Long userId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }
}
