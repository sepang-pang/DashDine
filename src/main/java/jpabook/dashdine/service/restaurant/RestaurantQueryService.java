package jpabook.dashdine.service.restaurant;

import jpabook.dashdine.domain.restaurant.Restaurant;

import java.util.List;

public interface RestaurantQueryService {
    List<Restaurant> findAllRestaurantByUserId(Long userId);
}
