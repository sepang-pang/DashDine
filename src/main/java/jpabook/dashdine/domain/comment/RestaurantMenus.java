package jpabook.dashdine.domain.comment;

import jpabook.dashdine.domain.order.OrderMenu;
import jpabook.dashdine.domain.restaurant.Restaurant;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RestaurantMenus {

    private final Restaurant restaurant;
    private final List<String> menuNames;

    public RestaurantMenus(List<OrderMenu> orderMenus) {
        this.restaurant = orderMenus.get(0).getMenu().getRestaurant();
        this.menuNames = orderMenus.stream()
                .map(om -> om.getMenu().getName())
                .distinct()
                .collect(Collectors.toList());
    }
}
