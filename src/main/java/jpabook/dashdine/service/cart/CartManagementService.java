package jpabook.dashdine.service.cart;

import jpabook.dashdine.domain.cart.Cart;
import jpabook.dashdine.domain.cart.CartMenu;
import jpabook.dashdine.domain.cart.CartMenuOption;
import jpabook.dashdine.domain.menu.Menu;
import jpabook.dashdine.domain.menu.Option;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.cart.CreateCartRequestDto;
import jpabook.dashdine.dto.response.cart.CartMenuOptionResponseDto;
import jpabook.dashdine.dto.response.cart.CartMenuResponseDto;
import jpabook.dashdine.dto.response.cart.CartResponseDto;
import jpabook.dashdine.repository.cart.CartRepository;
import jpabook.dashdine.service.cart.query.CartMenuOptionQueryService;
import jpabook.dashdine.service.cart.query.CartMenuQueryService;
import jpabook.dashdine.service.menu.MenuManagementService;
import jpabook.dashdine.service.menu.OptionManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Cart Management Service")
@Transactional
public class CartManagementService {

    private final CartRepository cartRepository;
    private final MenuManagementService menuManagementService;
    private final OptionManagementService optionManagementService;
    private final CartMenuQueryService cartMenuQueryService;
    private final CartMenuOptionQueryService cartMenuOptionQueryService;

    public void addCart(User user, CreateCartRequestDto createCartRequestDto) {
        // Cart 조회
        Cart cart = findOneCart(user);

        // Menu 조회
        Menu menu = menuManagementService.findOneMenu(createCartRequestDto.getMenuId());

        // CartMenu 조회
        List<CartMenu> cartMenus = cartMenuQueryService.findCartMenusByCartAndMenu(cart, menu);

        // Option 조회
        List<Option> options = optionManagementService.findOptionsInSet(createCartRequestDto.getOptions());

        // Cart Option Map 저장
        Map<Long, Set<CartMenuOption>> cartMenuIdToOptionsMap = findCartOptionMap(cartMenus);

        // optionIds 와 dto 의 options 가 같은지 확인
        if (checkAndIncreaseMatchingCartMenuCount(createCartRequestDto, cartMenus, cartMenuIdToOptionsMap)) return;

        saveCartMenuAndCartOptions(createCartRequestDto, cart, menu, options);
    }

    @Transactional(readOnly = true)
    public CartResponseDto readAllCart(User user) {

        System.out.println("// ======= Cart 조회 ======= //");
        Cart oneCart = cartRepository.findWithMenus(user.getCart().getId());
        CartResponseDto cartResponseDto = new CartResponseDto(oneCart);


        System.out.println("// ======= 장바구니 항목 조회 ======= //");
        List<Long> cartMenuIds = oneCart.getCartMenus().stream()
                .map(CartMenu::getId)
                .collect(Collectors.toList());

        System.out.println("// ======= 메뉴 옵션 조회 ======= //");
        List<CartMenuOption> cartMenuOptions =  cartMenuOptionQueryService.findCartOptionsByIdsToList(cartMenuIds);

        Map<Long, List<CartMenuOptionResponseDto>> optionsMap = cartMenuOptions.stream()
                .map(CartMenuOptionResponseDto::new)
                .collect(Collectors.groupingBy(o -> o.getCartMenuId()));

        System.out.println("// ======= CartMenuResponseDto 변환 ======= //");
        List<CartMenuResponseDto> cartMenuResponseDtos = oneCart.getCartMenus().stream()
                .map(cm -> {
                    CartMenuResponseDto dto = new CartMenuResponseDto(cm);
                    List<CartMenuOptionResponseDto> options = optionsMap.getOrDefault(cm.getId(), Collections.emptyList());
                    dto.setOptions(options);
                    return dto;
                })
                .collect(Collectors.toList());

        System.out.println("// ======= 옵션 조회 ======= //");

        cartResponseDto.setCartMenus(cartMenuResponseDtos);

        return cartResponseDto;
    }


    private Cart findOneCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 존재하지 않습니다."));
    }

    private Map<Long, Set<CartMenuOption>> findCartOptionMap(List<CartMenu> cartMenus) {
        // Cart Menu Id List 저장
        List<Long> cartMenuIds = cartMenus.stream()
                .map(CartMenu::getId)
                .collect(Collectors.toList());

        // Cart Menu Id 를 Key, Option Id 를 Set 형식의 Value 를 가지는 Map 생성
        return cartMenuOptionQueryService.findCartOptionsByIds(cartMenuIds)
                .stream()
                .collect(Collectors.groupingBy(cmo -> cmo.getCartMenu().getId(),
                        Collectors.toSet()));
    }

    private boolean checkAndIncreaseMatchingCartMenuCount(CreateCartRequestDto createCartRequestDto, List<CartMenu> cartMenus, Map<Long, Set<CartMenuOption>> cartMenuIdToOptionsMap) {
        for (CartMenu cartMenu : cartMenus) {
            Set<Long> optionIds = cartMenuIdToOptionsMap.get(cartMenu.getId()).stream()
                    .map(oi -> oi.getOption().getId())
                    .collect(Collectors.toSet());
            if (optionIds.equals(createCartRequestDto.getOptions())) {
                cartMenu.increaseCount(createCartRequestDto.getCount());
                return true;
            }
        }
        return false;
    }

    private void saveCartMenuAndCartOptions(CreateCartRequestDto createCartRequestDto, Cart cart, Menu menu, List<Option> options) {
        CartMenu newCartMenu = CartMenu.builder()
                .cart(cart)
                .menu(menu)
                .count(createCartRequestDto.getCount())
                .build();
        cartMenuQueryService.saveCartMenu(newCartMenu);

        List<CartMenuOption> cartMenuOptions = options.stream()
                .map(option -> CartMenuOption.builder()
                        .cartMenu(newCartMenu)
                        .option(option)
                        .build())
                .collect(Collectors.toList());
        cartMenuOptionQueryService.saveAllCartMenuOption(cartMenuOptions);
    }
}
