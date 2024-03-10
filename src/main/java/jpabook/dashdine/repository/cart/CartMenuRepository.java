package jpabook.dashdine.repository.cart;

import jpabook.dashdine.domain.cart.CartMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartMenuRepository extends JpaRepository<CartMenu, Long> {
    List<CartMenu> findByCartIdAndMenuId(Long cartId, Long menuId);

    List<CartMenu> findByCartId(Long cartId);
}
