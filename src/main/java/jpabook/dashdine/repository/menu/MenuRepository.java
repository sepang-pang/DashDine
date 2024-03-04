package jpabook.dashdine.repository.menu;

import jpabook.dashdine.domain.menu.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    @Query("select m.name from Menu m where m.restaurant.id = :restaurantId and m.isDeleted = false ")
    List<String> findMenuName(@Param("restaurantId")Long restaurantId);
}
