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
    public List<Order> findAllOrdersWithDelivery(Long userId) {
        return queryFactory
                .selectFrom(order)
                .join(order.delivery, delivery).fetchJoin()
                .where(order.user.id.eq(userId))
                .orderBy(order.createdAt.desc())
                .fetch();
    }

    @Override
    public List<Order> findAllOrdersByStatus(Long userId, OrderStatus status) {
        return queryFactory
                .selectFrom(order)
                .join(order.delivery, delivery).fetchJoin()
                .where(order.user.id.eq(userId)
                        .and(order.orderStatus.eq(status)))
                .orderBy(order.createdAt.desc())
                .fetch();
    }

    @Override
    public Optional<Order> findOneOrder(User user, Long orderId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(order)
                .join(order.delivery, delivery).fetchJoin()
                .where(order.user.id.eq(user.getId())
                        .and(order.id.eq(orderId)))
                .fetchOne());
    }
}

