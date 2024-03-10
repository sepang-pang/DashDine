package jpabook.dashdine.repository.cart;

import jpabook.dashdine.domain.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long id);

    @Query("select c from Cart c " +
            "left join fetch c.cartMenus cm " +
            "left join fetch cm.menu " +
            "where c.id = :cartId")
    Cart findWithMenus(@Param("cartId") Long cartId);
}
