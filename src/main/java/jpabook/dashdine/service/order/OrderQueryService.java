package jpabook.dashdine.service.order;

import jpabook.dashdine.domain.order.OrderMenu;
import jpabook.dashdine.domain.order.OrderMenuOption;

import java.util.List;

public interface OrderQueryService {
    List<OrderMenu> getOrderMenusById(Long orderId);
    List<OrderMenu> getOrderMenusByIdIn(List<Long> orderIds);
    List<OrderMenuOption> getOrderMenuOptions(List<Long> menuIds);
}
