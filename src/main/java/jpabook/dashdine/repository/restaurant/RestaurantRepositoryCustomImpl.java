package jpabook.dashdine.repository.restaurant;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.dashdine.dto.response.restaurant.RestaurantDetailsForm;
import jpabook.dashdine.dto.response.restaurant.RestaurantForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static jpabook.dashdine.domain.restaurant.QRestaurant.restaurant;

@Repository
@RequiredArgsConstructor
public class RestaurantRepositoryCustomImpl implements RestaurantRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public RestaurantDetailsForm findRestaurantDetailsFormById(Long restaurantId) {
        return jpaQueryFactory
                .select(Projections.constructor(RestaurantDetailsForm.class,
                        restaurant.name,
                        restaurant.tel,
                        restaurant.info,
                        restaurant.openingTime,
                        restaurant.closingTime,
                        restaurant.isOperating,
                        restaurant.category.name))
                .from(restaurant)
                .where(restaurant.id.eq(restaurantId)
                        .and(restaurant.isDeleted.eq(false)))
                .fetchOne();

    }
}
