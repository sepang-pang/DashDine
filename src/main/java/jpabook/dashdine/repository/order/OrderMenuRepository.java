package jpabook.dashdine.repository.order;

import jpabook.dashdine.domain.order.OrderMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderMenuRepository extends JpaRepository<OrderMenu, Long> {

    @Query("select om from OrderMenu om " +
            "left join fetch om.menu " +
            "where om.order.id in :orderIds")
    List<OrderMenu> findAllOrderMenusByIdIn(@Param("orderIds")List<Long> orderIds);

    @Query("select om from OrderMenu om " +
            "left join fetch om.menu " +
            "where om.order.id = :orderId")
    List<OrderMenu> findAllOrderMenuById(@Param("orderId")Long orderId);

    @Query("select om from OrderMenu om " +
            "left join fetch om.order " +
            "left join fetch om.menu m " +
            "left join m.restaurant r " +
            "where r.id in :restaurantIds")
    List<OrderMenu> findAllOrderMenuByRestaurantIds(@Param("restaurantIds") List<Long> restaurantIds);
}
