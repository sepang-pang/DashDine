package jpabook.dashdine.service.cart.query;

import jpabook.dashdine.domain.cart.Cart;
import jpabook.dashdine.domain.cart.CartMenu;
import jpabook.dashdine.domain.menu.Menu;
import jpabook.dashdine.repository.cart.CartMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartMenuQueryService {

    private final CartMenuRepository cartMenuRepository;

    // Cart Menu 조회 (Cart Id, Menu Id)
    public List<CartMenu> findCartMenusByCartAndMenu(Cart cart, Menu menu) {
        return cartMenuRepository.findByCartIdAndMenuId(cart.getId(), menu.getId());
    }

    // Cart Menu 저장
    @Transactional
    public void saveCartMenu(CartMenu cartMenu) {
        cartMenuRepository.save(cartMenu);
    }

    // Cart Menu 삭제
    @Transactional
    public void deleteCartMenu(CartMenu cartMenu) {
        cartMenuRepository.delete(cartMenu);
    }

    public CartMenu findOneCartMenu(Long cartMenuId) {
        return cartMenuRepository.findByIdAndIsDeletedFalse(cartMenuId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));
    }
}