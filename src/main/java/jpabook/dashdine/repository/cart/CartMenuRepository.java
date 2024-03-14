package jpabook.dashdine.repository.cart;

import jpabook.dashdine.domain.cart.CartMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartMenuRepository extends JpaRepository<CartMenu, Long> {

    Optional<CartMenu> findByIdAndIsDeletedFalse(Long cartMenuId);

    @Query("select cm from CartMenu cm where cm.cart.id = :cartId and cm.menu.id = :menuId and cm.isDeleted = false")
    List<CartMenu> findByCartIdAndMenuId(@Param("cartId") Long cartId, @Param("menuId") Long menuId);

    @Query("select cm from CartMenu cm where cm.id in :cartMenuIds and cm.isDeleted = false")
    List<CartMenu> findCartMenus(@Param("cartMenuIds") List<Long> cartMenuIds);

    @Modifying
    @Query("delete from CartMenu cm where cm in :cartMenus")
    void deleteAllByCartMenus(@Param("cartMenus") List<CartMenu> cartMenus);
}
