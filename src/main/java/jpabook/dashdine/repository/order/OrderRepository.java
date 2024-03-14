package jpabook.dashdine.repository.order;

import jpabook.dashdine.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
