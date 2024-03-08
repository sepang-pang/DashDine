package jpabook.dashdine.repository.cart;

import jpabook.dashdine.domain.cart.CartMenuOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CartMenuOptionRepository extends JpaRepository<CartMenuOption, Long> {

    List<CartMenuOption> findByCartMenuId(Long cartMenuId);

    @Query("select cmo from CartMenuOption cmo where cmo.cartMenu.id in :cartMenuIds")
    Set<CartMenuOption> findCartMenuOptionByMenuIds(@Param("cartMenuIds")List<Long> cartMenuIds);

    @Query("select cmo from CartMenuOption cmo " +
            "left join fetch cmo.option " +
            "where cmo.cartMenu.id in :cartMenuIds")
    List<CartMenuOption> findCartMenuOptionByMenuIdsToList(@Param("cartMenuIds")List<Long> cartMenuIds);


}
