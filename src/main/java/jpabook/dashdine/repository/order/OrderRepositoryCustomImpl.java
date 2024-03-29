package jpabook.dashdine.repository.order;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.dashdine.domain.order.Order;
import jpabook.dashdine.domain.order.OrderStatus;
import jpabook.dashdine.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static jpabook.dashdine.domain.order.QDelivery.delivery;
import static jpabook.dashdine.domain.order.QOrder.order;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Order> findOrdersByUserId(Long userId) {
        return queryFactory
                .selectFrom(order)
                .join(order.delivery, delivery).fetchJoin()
                .where(order.user.id.eq(userId)
                        .and(order.isDeleted.eq(false)))
                .orderBy(order.createdAt.desc())
                .fetch();
    }

    @Override
    public List<Order> findOrdersByUserIdAndOrderStatus(Long userId, OrderStatus status) {
        return queryFactory
                .selectFrom(order)
                .join(order.delivery, delivery).fetchJoin()
                .where(order.user.id.eq(userId)
                        .and(order.orderStatus.eq(status))
                        .and(order.isDeleted.eq(false)))
                .orderBy(order.createdAt.desc())
                .fetch();
    }

    @Override
    public List<Order> findOrdersByOrderIdIn(List<Long> orderIds) {
        return queryFactory
                .selectFrom(order)
                .join(order.delivery, delivery).fetchJoin()
                .where(order.id.in(orderIds)
                        .and(order.isDeleted.eq(false)))
                .orderBy(order.createdAt.desc())
                .fetch();
    }

    @Override
    public List<Order> findOrdersByIdInAndOrderStatus(List<Long> orderIds, OrderStatus orderStatus) {
        return queryFactory
                .selectFrom(order)
                .join(order.delivery, delivery).fetchJoin()
                .where(order.id.in(orderIds)
                        .and(order.orderStatus.eq(orderStatus))
                        .and(order.isDeleted.eq(false)))
                .orderBy(order.createdAt.desc())
                .fetch();
    }

    @Override
    public Optional<Order> findOrderById(Long orderId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(order)
                .join(order.delivery, delivery).fetchJoin()
                .where(order.id.eq(orderId)
                        .and(order.isDeleted.eq(false)))
                .fetchOne());
    }

    @Override
    public Optional<Order> findOrderByIdAndOrderStatusPending(Long orderId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(order)
                .join(order.delivery, delivery).fetchJoin()
                .where(order.id.eq(orderId)
                        .and(order.orderStatus.eq(OrderStatus.PENDING))
                        .and(order.isDeleted.eq(false)))
                .fetchOne());
    }
}

