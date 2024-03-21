package jpabook.dashdine.repository.restaurant;

import jpabook.dashdine.dto.response.restaurant.RestaurantDetailsForm;

public interface RestaurantRepositoryCustom {
    RestaurantDetailsForm findRestaurantDetailsFormById(Long restaurantId);
}
