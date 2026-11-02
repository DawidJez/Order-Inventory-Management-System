package pl.dawjez.orderinventory.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // A customer's own orders, newest first.
    List<Order> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    // Every order in the system, newest first. Used only for ADMIN.
    List<Order> findAllByOrderByCreatedAtDesc();
}
