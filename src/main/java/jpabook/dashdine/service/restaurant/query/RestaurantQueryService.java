package jpabook.dashdine.service.restaurant.query;

import jpabook.dashdine.domain.restaurant.Restaurant;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.repository.restaurant.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantQueryService {

    private final RestaurantRepository restaurantRepository;

    public List<Restaurant> findAllRestaurants(Long userId) {
        List<Restaurant> findRestaurant = restaurantRepository.findRestaurantsByUserId(userId);

        if (findRestaurant == null || findRestaurant.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 항목입니다.");
        }
        return findRestaurant;
    }

    public Restaurant findOneRestaurant(User user, Long restaurantId) {
        return restaurantRepository.findByUserIdAndIdAndIsDeletedFalse(user.getId(), restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 본인 소유의 가게가 아닙니다"));
    }
}
