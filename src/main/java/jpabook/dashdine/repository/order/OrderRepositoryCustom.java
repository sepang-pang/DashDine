package jpabook.dashdine.repository.order;

import jpabook.dashdine.domain.order.Order;
import jpabook.dashdine.domain.order.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryCustom {

    /*
    유저의 전체 주문내역 조회

    delivery 와 fetch join
    주문 생성일 기준 내림차순 정렬 -> 최신순으로 조회
    */
    List<Order> findOrdersByUserId(Long userId);

    List<Order> findOrdersByUserIdAndOrderStatus(Long userId, OrderStatus status);

    List<Order> findOrdersByOrderIdIn(List<Long> orderIds);

    List<Order> findOrdersByIdInAndOrderStatus(List<Long> orderIds, OrderStatus orderStatus);

    Optional<Order> findOrderById(Long orderId);

    Optional<Order> findOrderByIdAndOrderStatusPending(Long orderId);

}
