package jpabook.dashdine.service.order.query;

import jpabook.dashdine.domain.order.OrderMenu;
import jpabook.dashdine.domain.restaurant.Restaurant;
import jpabook.dashdine.repository.order.OrderMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderMenuQueryService {

    private final OrderMenuRepository orderMenuRepository;

    public List<OrderMenu> findAllOrderMenus(Long orderId) {
        List<OrderMenu> findOrderMenus = orderMenuRepository.findOrderMenusByOrderId(orderId);

        if(findOrderMenus == null || findOrderMenus.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 항목입니다.");
        }

        return findOrderMenus;
    }

    public List<OrderMenu> findAllOrderMenusByOrderIds(List<Long> orderIds) {

        List<OrderMenu> findOrderMenus = orderMenuRepository.findOrderMenusByOrderIdIn(orderIds);

        if(findOrderMenus == null || findOrderMenus.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 항목입니다.");
        }

        return findOrderMenus;
    }

    public List<OrderMenu> findAllOrderMenusByRestaurantIds(List<Long> restaurantIds) {

        List<OrderMenu> findOrderMenus = orderMenuRepository.findOrderMenusByRestaurantIdIn(restaurantIds);

        if (findOrderMenus == null || findOrderMenus.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 항목입니다.");
        }
        return findOrderMenus;
    }
}
