package jpabook.dashdine.repository.order;

import jpabook.dashdine.domain.order.Order;
import jpabook.dashdine.domain.order.OrderStatus;
import jpabook.dashdine.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryCustom {

    /*
    유저의 전체 주문내역 조회

    delivery 와 fetch join
    주문 생성일 기준 내림차순 정렬 -> 최신순으로 조회
    */
    List<Order> findAllOrdersWithDelivery(Long userId);

    List<Order> findAllOrdersByStatus(Long userId, OrderStatus status);

    List<Order> findAllOrdersByIdIn(List<Long> orderIds);

    List<Order> findAllOrdersByIdInAndStatus(List<Long> orderIds, OrderStatus orderStatus);

    Optional<Order> findOneOrder(Long orderId);

    Optional<Order> findOneOrderById(Long orderId);

}
