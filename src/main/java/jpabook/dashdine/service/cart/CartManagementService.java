package jpabook.dashdine.service.cart;

import jpabook.dashdine.domain.cart.Cart;
import jpabook.dashdine.domain.cart.CartMenu;
import jpabook.dashdine.domain.cart.CartMenuOption;
import jpabook.dashdine.domain.menu.Menu;
import jpabook.dashdine.domain.menu.Option;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.cart.CreateCartRequestDto;
import jpabook.dashdine.repository.cart.CartMenuOptionRepository;
import jpabook.dashdine.repository.cart.CartMenuRepository;
import jpabook.dashdine.repository.cart.CartRepository;
import jpabook.dashdine.repository.menu.OptionRepository;
import jpabook.dashdine.service.menu.MenuManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Cart Management Service")
@Transactional
public class CartManagementService {

    private final CartRepository cartRepository;
    private final CartMenuRepository cartMenuRepository;
    private final CartMenuOptionRepository cartMenuOptionRepository;
    private final OptionRepository optionRepository;
    private final MenuManagementService menuManagementService;

    public void addCart(User user, CreateCartRequestDto createCartRequestDto) {
        // Cart 조회
        System.out.println("// ============= Cart 조회 ============= //");
        Cart cart = findOneCart(user);

        // Menu 조회
        System.out.println("// ============= Menu 조회 ============= //");
        Menu menu = menuManagementService.findOneMenu(createCartRequestDto.getMenuId());

        // CartMenu 조회
        System.out.println("// ============= Cart Menu 조회 ============= //");
        List<CartMenu> findCartMenus = cartMenuRepository.findByCartIdAndMenuId(cart.getId(), menu.getId());

        // Option 조회
        System.out.println("// ============= Option 조회 ============= //");
        List<Option> options = optionRepository.findByIdIn(createCartRequestDto.getOptions());

        // cart menu id 를 이용하여 cart menu option 의 option id 를 추출

        Map<Long, Set<Long>> optionidsMap = new HashMap<>();

        System.out.println("// ============= OptionIds 조회 ============= //");
        for(CartMenu cartMenu : findCartMenus) {
            optionidsMap.put(cartMenu.getId(), cartMenuOptionRepository.findOptionIds(cartMenu.getId()));
        }

        // optionIds 와 dto 의 options 가 같은지 확인
        System.out.println("// ============= optionIds 와 dto 의 options 가 같은지 확인 ============= //");
        for(CartMenu cartMenu : findCartMenus) {
            if (optionidsMap.get(cartMenu.getId()).equals(createCartRequestDto.getOptions())) {
                cartMenu.increaseCount(createCartRequestDto.getCount());
                return;
            }
        }

        System.out.println("// ============= Cart Menu 저장 ============= //");
        CartMenu newCartMenu = CartMenu.builder()
                .cart(cart)
                .menu(menu)
                .count(createCartRequestDto.getCount())
                .build();
        cartMenuRepository.save(newCartMenu);

        System.out.println("// ============= Option 저장 ============= //");
        for(Option option : options) {
            CartMenuOption cartMenuOption = CartMenuOption.builder()
                    .cartMenu(newCartMenu)
                    .option(option)
                    .build();
            cartMenuOptionRepository.save(cartMenuOption);
        }
    }

    private Cart findOneCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 존재하지 않습니다."));
    }


    // dto 를 통해 다수의 옵션이 요청되면, 해당 옵션의 id 값을 리스트로 받는다
    // 메뉴측 메뉴옵션에서 in 절을 통해, 전달 받은 옵션 id 의 객체를 가져온다.
    // 이후 가져온 id 를 cart_menu 에 함께 저장한다.
}
