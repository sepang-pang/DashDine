package jpabook.dashdine.repository.order;

import jpabook.dashdine.domain.order.OrderMenuOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderMenuOptionRepository extends JpaRepository<OrderMenuOption, Long> {

    @Query("select omo from OrderMenuOption omo " +
            "left join fetch omo.option " +
            "where omo.orderMenu.id in :orderMenuIds")
    List<OrderMenuOption> findAllOrderOption(@Param("orderMenuIds")List<Long> orderMenuIds);
}
