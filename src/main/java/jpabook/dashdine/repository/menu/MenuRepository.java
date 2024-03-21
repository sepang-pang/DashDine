package jpabook.dashdine.repository.menu;

import jpabook.dashdine.domain.menu.Menu;
import jpabook.dashdine.dto.response.menu.MenuDetailsForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long>, MenuRepositoryCustom {

    @Query("select new jpabook.dashdine.dto.response.menu.MenuDetailsForm(m.id, m.name, m.price, m.content, m.image, m.stackQuantity) from Menu m where m.restaurant.id = :restaurantId and m.isDeleted = false")
    List<MenuDetailsForm> findMenuDetailsFormsByRestaurantId(@Param("restaurantId")Long restaurantId);

    @Query("select m from Menu m where m.id =:menuId and m.isDeleted = false")
    Optional<Menu> findMenuById(@Param("menuId")Long menuId);

    @Query("select new jpabook.dashdine.dto.response.menu.MenuDetailsForm(m.id, m.name, m.price, m.content, m.image, m.stackQuantity) from Menu m where m.id = :menuId and m.isDeleted = false")
    MenuDetailsForm findMenuDetailsFormById(@Param("menuId")Long menuId);

    @Query("select m.name from Menu m where m.restaurant.id = :restaurantId and m.isDeleted = false")
    List<String> findMenuNameByRestaurantId(@Param("restaurantId")Long restaurantId);

}
