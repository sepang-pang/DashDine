package jpabook.dashdine.service.cart;

import jpabook.dashdine.domain.cart.Cart;
import jpabook.dashdine.domain.cart.CartMenu;
import jpabook.dashdine.domain.menu.Menu;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.cart.CreateCartRequestDto;
import jpabook.dashdine.repository.cart.CartMenuRepository;
import jpabook.dashdine.repository.cart.CartRepository;
import jpabook.dashdine.service.menu.MenuManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Cart Management Service")
@Transactional
public class CartManagementService {

    private final CartRepository cartRepository;
    private final CartMenuRepository cartMenuRepository;
    private final MenuManagementService menuManagementService;

    public void addCart(User user, CreateCartRequestDto createCartRequestDto) {
        // Cart 조회
        Cart cart = findOneCart(user);

        // Menu 조회
        Menu menu = menuManagementService.findOneMenu(createCartRequestDto.getMenuId());

        // CartMenu 조회
        CartMenu findCartMenu = cartMenuRepository.findByCartIdAndMenuId(cart.getId(), menu.getId());

        if(findCartMenu == null) {
            CartMenu cartMenu = CartMenu.builder()
                    .count(createCartRequestDto.getCount())
                    .cart(cart)
                    .menu(menu)
                    .build();
            cartMenuRepository.save(cartMenu);
        } else {
            findCartMenu.increaseCount(createCartRequestDto.getCount());
        }
    }

    private Cart findOneCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 존재하지 않습니다."));
    }
}
