package jpabook.dashdine.repository.cart;

import jpabook.dashdine.domain.cart.CartMenu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartMenuRepository extends JpaRepository<CartMenu, Long> {
    CartMenu findByCartIdAndMenuId(Long cartId, Long menuId);
}
