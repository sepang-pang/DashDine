package jpabook.dashdine.repository.cart;

import jpabook.dashdine.domain.cart.CartMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartMenuRepository extends JpaRepository<CartMenu, Long> {

    @Query("select cm from CartMenu cm where cm.cart.id = :cartId and cm.menu.id = :menuId")
    List<CartMenu> findByCartIdAndMenuId(@Param("cartId") Long cartId, @Param("menuId") Long menuId);

    @Query("select cm from CartMenu cm " +
            "left join fetch cm.menu " +
            "where cm.id in :cartMenuIds")
    List<CartMenu> findCartMenus(@Param("cartMenuIds") List<Long> cartMenuIds);

    @Modifying
    @Query("delete from CartMenu cm where cm in :cartMenus")
    void deleteAllByCartMenus(@Param("cartMenus") List<CartMenu> cartMenus);

    @Query("select cm from CartMenu cm " +
            "left join fetch cm.menu " +
            "where cm.cart.id = :cartId")
    List<CartMenu> findCartMenusByCartId(@Param("cartId") Long cartId);
}

