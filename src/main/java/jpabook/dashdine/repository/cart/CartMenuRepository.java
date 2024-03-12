package jpabook.dashdine.repository.cart;

import jpabook.dashdine.domain.cart.CartMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartMenuRepository extends JpaRepository<CartMenu, Long> {

    @Query("select cm from CartMenu cm where cm.cart.id = :cartId and cm.menu.id = :menuId and cm.isDeleted = false")
    List<CartMenu> findByCartIdAndMenuId(@Param("cartId") Long cartId, @Param("menuId") Long menuId);
}
