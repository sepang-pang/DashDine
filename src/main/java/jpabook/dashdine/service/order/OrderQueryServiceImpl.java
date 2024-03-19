package jpabook.dashdine.service.order;

import jpabook.dashdine.domain.order.OrderMenu;
import jpabook.dashdine.domain.order.OrderMenuOption;
import jpabook.dashdine.repository.order.OrderMenuOptionRepository;
import jpabook.dashdine.repository.order.OrderMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryServiceImpl implements OrderQueryService {

    private final OrderMenuRepository orderMenuRepository;
    private final OrderMenuOptionRepository orderMenuOptionRepository;

    @Override
    public List<OrderMenu> getOrderMenusById(Long orderId) {
        List<OrderMenu> findOrderMenus = orderMenuRepository.findAllOrderMenuById(orderId);

        if(findOrderMenus == null || findOrderMenus.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 항목입니다.");
        }

        return findOrderMenus;
    }

    @Override
    public List<OrderMenu> getOrderMenusByIdIn(List<Long> orderIds) {
        List<OrderMenu> findOrderMenus = orderMenuRepository.findAllOrderMenusByIdIn(orderIds);

        if(findOrderMenus == null || findOrderMenus.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 항목입니다.");
        }

        return findOrderMenus;
    }

    @Override
    public List<OrderMenu> findAllOrderMenusByRestaurantIds(List<Long> restaurantIds) {
        List<OrderMenu> findOrderMenus = orderMenuRepository.findAllOrderMenuByRestaurantIds(restaurantIds);

        if (findOrderMenus == null || findOrderMenus.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 항목입니다.");
        }
        return findOrderMenus;
    }

    @Override
    public List<OrderMenuOption> getOrderMenuOptions(List<Long> menuIds) {
        List<OrderMenuOption> findOrderOptions = orderMenuOptionRepository.findAllOrderOption(menuIds);

        if (findOrderOptions == null || findOrderOptions.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 항목입니다.");
        }

        return findOrderOptions;
    }
}
