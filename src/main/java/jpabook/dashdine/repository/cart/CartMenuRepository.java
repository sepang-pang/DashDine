package jpabook.dashdine.repository.cart;

import jpabook.dashdine.domain.cart.CartMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartMenuRepository extends JpaRepository<CartMenu, Long> {
    List<CartMenu> findByCartIdAndMenuId(Long cartId, Long menuId);

    @Query("select cm from CartMenu cm " +
            "left join fetch cm.menu " +
            "where cm.id in :cartMenuIds")
    List<CartMenu> findByIdIn(@Param("cartMenuIds") List<Long> cartMenuIds);

}
