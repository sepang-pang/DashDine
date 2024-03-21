package jpabook.dashdine.repository.menu;

import jpabook.dashdine.dto.response.menu.MenuForm;

import java.util.List;

public interface MenuRepositoryCustom {

    List<MenuForm> findAllMenuFormsByRestaurantId(Long restaurantId);
}
