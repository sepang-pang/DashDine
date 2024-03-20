package jpabook.dashdine.repository.cart;

import jpabook.dashdine.domain.cart.CartMenu;
import jpabook.dashdine.domain.cart.CartMenuOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartMenuOptionRepository extends JpaRepository<CartMenuOption, Long> {

    @Query("select cmo from CartMenuOption cmo " +
            "left join fetch cmo.option " +
            "where cmo.cartMenu.id in :cartMenuIds")
    List<CartMenuOption> findCartMenuOptionByMenuIds(@Param("cartMenuIds")List<Long> cartMenuIds);

    @Modifying
    @Query("delete from CartMenuOption cmo where cmo.cartMenu in :cartMenus")
    void deleteAllByCartMenu(@Param("cartMenus")List<CartMenu> cartMenus);

}
