package jpabook.dashdine.repository.Restaurant;

import jpabook.dashdine.domain.restaurant.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}
