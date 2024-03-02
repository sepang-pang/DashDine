package jpabook.dashdine.repository.Restaurant;

import jpabook.dashdine.domain.restaurant.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    public Restaurant findByIdAndName(Long id, String name);

    @Query("select r.name from Restaurant r where r.user.id = :userId")
    List<String> findRestaurantNameByUserId(@Param("userId")Long id);
}
