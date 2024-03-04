package jpabook.dashdine.repository.menu;

import jpabook.dashdine.domain.menu.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query("select m from Menu m where m.restaurant.user.id = :userId and m.id =:menuId and m.isDeleted = false")
    Optional<Menu> findMenuByUserIdAndMenuId(@Param("userId")Long userId, @Param("menuId")Long menuId);

    @Query("select m.name from Menu m where m.restaurant.id = :restaurantId and m.isDeleted = false")
    List<String> findMenuName(@Param("restaurantId")Long restaurantId);
}
