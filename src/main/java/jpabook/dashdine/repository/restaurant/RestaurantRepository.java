package jpabook.dashdine.repository.restaurant;

import jpabook.dashdine.domain.restaurant.Restaurant;
import jpabook.dashdine.dto.response.restaurant.RestaurantForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, RestaurantRepositoryCustom {

    Optional<Restaurant> findByUserIdAndIdAndIsDeletedFalse(Long userId, Long restaurantId);

    @Query("select r.name from Restaurant r where r.user.id = :userId and r.isDeleted = false")
    List<String> findRestaurantNameByUserId(@Param("userId") Long id);

    @Query("select new jpabook.dashdine.dto.response.restaurant.RestaurantForm(r.name, r.tel, r.info, r.openingTime, r.closingTime, r.isOperating, r.category.name) from Restaurant r where r.user.id = :userId and r.isDeleted = false")
    List<RestaurantForm> findRestaurantListByUserId(@Param("userId") Long userId);

    @Query("select new jpabook.dashdine.dto.response.restaurant.RestaurantForm(r.name, r.tel, r.info, r.openingTime, r.closingTime, r.isOperating, r.category.name) from Restaurant r where r.category.id = :categoryId and r.isDeleted = false")
    List<RestaurantForm> findRestaurantListByCategoryId(@Param("categoryId") Long categoryId);

    @Query("select r from Restaurant r where r.user.id = :userId and r.isDeleted = false ")
    List<Restaurant> findAllRestaurantsByUserId(@Param("userId") Long userId);
}
