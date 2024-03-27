package jpabook.dashdine.service.order.query;

import jpabook.dashdine.domain.order.Order;
import jpabook.dashdine.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderRepository orderRepository;

    public Order findOneOrder(Long orderId) {
        return orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));
    }
}
