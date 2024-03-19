package jpabook.dashdine.service.restaurant;

import jpabook.dashdine.domain.restaurant.Restaurant;
import jpabook.dashdine.repository.restaurant.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantQueryServiceImpl implements RestaurantQueryService {

    private final RestaurantRepository restaurantRepository;
    @Override
    public List<Restaurant> findAllRestaurantByUserId(Long userId) {
        List<Restaurant> findRestaurant = restaurantRepository.findAllRestaurantsByUserId(userId);

        if (findRestaurant == null || findRestaurant.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 항목입니다.");
        }
        return findRestaurant;
    }
}
