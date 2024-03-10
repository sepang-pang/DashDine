package jpabook.dashdine.repository.cart;

import jpabook.dashdine.domain.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long id);
    @Query("SELECT c FROM Cart c " +
            "LEFT JOIN FETCH c.cartMenus cm " +
            "LEFT join FETCH cm.menu " +
            "WHERE c.id = :cartId")
    Cart findWithMenus(@Param("cartId") Long cartId);


}
