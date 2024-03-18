package jpabook.dashdine.repository.order;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.dashdine.domain.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}

