package jpabook.dashdine.service.restaurant.query;

import jpabook.dashdine.domain.restaurant.Restaurant;
import jpabook.dashdine.repository.restaurant.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantQueryService {

    private final RestaurantRepository restaurantRepository;

    public List<Restaurant> findAllRestaurantByUserId(Long userId) {
        List<Restaurant> findRestaurant = restaurantRepository.findRestaurantsByUserId(userId);

        if (findRestaurant == null || findRestaurant.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 항목입니다.");
        }
        return findRestaurant;
    }
}
