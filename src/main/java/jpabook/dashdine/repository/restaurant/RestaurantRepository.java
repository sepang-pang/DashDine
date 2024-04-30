package jpabook.dashdine.repository.restaurant;

import jpabook.dashdine.domain.restaurant.Restaurant;
import jpabook.dashdine.dto.response.restaurant.RestaurantForm;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, RestaurantRepositoryCustom {

    Optional<Restaurant> findByUserIdAndIdAndIsDeletedFalse(Long userId, Long restaurantId);

    @Query("select r.name from Restaurant r where r.user.id = :userId and r.isDeleted = false")
    List<String> findRestaurantNameByUserId(@Param("userId") Long id);

    @Query("select new jpabook.dashdine.dto.response.restaurant.RestaurantForm(r.id, r.name, r.tel, r.info, r.openingTime, r.closingTime, r.isOperating, r.minimumPrice, r.category.name, r.category.id, r.address.street, r.address.streetDetail) from Restaurant r where r.user.id = :userId and r.isDeleted = false")
    List<RestaurantForm> findRestaurantFormsByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT * " +
            "FROM restaurant r " +
            "WHERE r.category_id = :categoryId " +
            "AND r.is_deleted = false " +
            "AND ST_Distance_Sphere(r.point, :userPoint) <= (:radius * 1000)" +
            "ORDER BY ST_Distance_Sphere(r.point, :userPoint) ASC", nativeQuery = true)
    List<Restaurant> findRestaurantsByCategoryId(@Param("userPoint") Point userPoint, @Param("radius") double radius, @Param("categoryId") Long categoryId);

    @Query("select r from Restaurant r where r.user.id = :userId and r.isDeleted = false ")
    List<Restaurant> findRestaurantsByUserId(@Param("userId") Long userId);

    @Query("select new jpabook.dashdine.dto.response.restaurant.RestaurantForm(r.id, r.name, r.tel, r.info, r.openingTime, r.closingTime, r.isOperating, r.minimumPrice, r.category.name, r.category.id, r.address.zipcode, r.address.street, r.address.streetDetail) from Restaurant r where r.id = :restaurantId and r.user.id = :userId and r.isDeleted = false")
    RestaurantForm findRestaurantByRestaurantId(@Param("userId") Long userId,
                                                @Param("restaurantId") Long restaurantId);
}
