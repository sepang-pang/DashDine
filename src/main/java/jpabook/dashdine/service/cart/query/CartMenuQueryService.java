package jpabook.dashdine.service.cart.query;

import jpabook.dashdine.domain.cart.Cart;
import jpabook.dashdine.domain.cart.CartMenu;
import jpabook.dashdine.domain.menu.Menu;
import jpabook.dashdine.repository.cart.CartMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartMenuQueryService {

    private final CartMenuRepository cartMenuRepository;


    public CartMenu findOneCartMenu(Long cartMenuId) {
        return cartMenuRepository.findById(cartMenuId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));
    }

    public List<CartMenu> findAllCartMenus(Cart cart, Menu menu) {
        return cartMenuRepository.findCartMenusByCartIdAndMenuId(cart.getId(), menu.getId());
    }

    public List<CartMenu> findAllCartMenus(List<Long> cartMenuIds) {
        return cartMenuRepository.findCartMenusIdIn(cartMenuIds);
    }

    public List<CartMenu> findAllCartMenus(Cart cart) {
        return cartMenuRepository.findCartMenusByCartId(cart.getId());
    }

    // Cart Menu 저장
    public void saveCartMenu(CartMenu cartMenu) {
        cartMenuRepository.save(cartMenu);
    }

    // Cart Menu 삭제
    public void deleteCartMenu(CartMenu cartMenu) {
        cartMenuRepository.delete(cartMenu);
    }

    public void deleteCartMenus(List<CartMenu> cartMenus) {
        cartMenuRepository.deleteAllCartMenus(cartMenus);
    }
}
