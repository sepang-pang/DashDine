package jpabook.dashdine.service.cart;

import jpabook.dashdine.domain.cart.Cart;
import jpabook.dashdine.domain.cart.CartMenu;
import jpabook.dashdine.domain.cart.CartMenuOption;
import jpabook.dashdine.domain.menu.Menu;
import jpabook.dashdine.domain.menu.Option;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.cart.CreateCartRequestDto;
import jpabook.dashdine.dto.request.cart.UpdateCartRequestDto;
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
        System.out.println("// ======= Cart 조회 ======= //");
        Cart cart = findOneCart(user);

        // Menu 조회
        System.out.println("// ======= Menu 조회 ======= //");
        Menu menu = menuManagementService.findOneMenu(createCartRequestDto.getMenuId());

        // Option 조회
        System.out.println("// ======= Option 조회 ======= //");
        List<Option> options = optionManagementService.findOptions(createCartRequestDto.getOptions());

        // CartMenu 조회
        System.out.println("// ======= Cart Menu 조회 ======= //");
        List<CartMenu> cartMenus = cartMenuQueryService.findCartMenusByCartAndMenu(cart, menu);

        // Cart Option Map 저장
        System.out.println("// ======= Cart 조회 ======= //");
        Map<Long, List<CartMenuOption>> cartMenuIdToOptionsMap = findCartOptionMap(cartMenus);

        // optionIds 와 dto 의 options 가 같은지 확인
        if (checkAndIncreaseMatchingCartMenuCount(createCartRequestDto, cartMenus, cartMenuIdToOptionsMap)) return;

        saveCartMenuAndCartOptions(createCartRequestDto, cart, menu, options);
    }

    @Transactional(readOnly = true)
    public CartResponseDto readAllCart(User user) {
        // 장바구니 조회 ( 장바구니 목록과 각 목록의 메뉴 Fetch Join )
        Cart oneCart = cartRepository.findWithMenus(user.getCart().getId());
        CartResponseDto cartResponseDto = new CartResponseDto(oneCart);

        // 장바구니 목록의 Id 를 Key, 메뉴의 옵션들을 Value 로 갖는 Map 생성
        Map<Long, List<CartMenuOption>> cartOptionMap = findCartOptionMap(oneCart.getCartMenus());

        // 장바구니 목록 dto 생성
        List<CartMenuResponseDto> cartMenuResponseDtos = getCartMenuResponseDtos(oneCart, cartOptionMap);

        cartResponseDto.updateCartDto(cartMenuResponseDtos);

        return cartResponseDto;
    }

    public void updateCart(Long cartMenuId, UpdateCartRequestDto updateCartRequestDto) {
        System.out.println("// ========== 장바구니 목록 조회 ========== //");
        CartMenu findCartMenu = cartMenuQueryService.findCartMenuById(cartMenuId);

        findCartMenu.updateCount(updateCartRequestDto.getCount());

        // 요청한 옵션에서 기존 cart menu option 에 존재하면, request dto 에서 제거
        // 요청한 옵션에서 기존 cart menu option 에 존재하지 않다면, cart menu option 에서 제거 O

        // 기존 cart menu option 에 1, 2, 3, 4 이 존재
        // 그런데 요청 보낸 건 2, 3, 4, 5
        // 이때 cart menu option 에서는 1을 삭제

        findCartMenu.getCartMenuOptions().removeIf(cartMenuOption ->
                !updateCartRequestDto.getOptions().contains(cartMenuOption.getOption().getId()));

        // 2, 3, 4, 5 를 요청했는데
        // 기존 cart menu option 에 2, 3, 4 가 존재한다면
        // 요청한 2, 3, 4, 5에서 2, 3, 4 를 제거

        List<Long> cartMenuOptionIds = findCartMenu.getCartMenuOptions().stream()
                .map(cmo -> cmo.getOption().getId()).toList();

        List<Long> options = updateCartRequestDto.getOptions();

        options.removeIf(cartMenuOptionIds::contains);

        List<Option> optionList = optionManagementService.findOptions(updateCartRequestDto.getOptions());

        List<CartMenuOption> cartMenuOptions = optionList.stream()
                .map(option -> CartMenuOption.builder()
                        .cartMenu(findCartMenu)
                        .option(option)
                        .build())
                .collect(Collectors.toList());
        cartMenuOptionQueryService.saveAllCartMenuOption(cartMenuOptions);

    }


    // ============ Private 메서드 ============ //
    private Cart findOneCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 존재하지 않습니다."));
    }

    private Map<Long, List<CartMenuOption>> findCartOptionMap(List<CartMenu> cartMenus) {
        // Cart Menu Id List 저장
        List<Long> cartMenuIds = cartMenus.stream()
                .map(CartMenu::getId)
                .collect(Collectors.toList());

        // Cart Menu Id 를 Key, Option Id 를 Value 로 가지는 Map 생성
        // 이때 Option Fetch Join
        return cartMenuOptionQueryService.findCartOptionsByIds(cartMenuIds)
                .stream()
                .collect(Collectors.groupingBy(cmo -> cmo.getCartMenu().getId()));
    }

    // ============ 전체조회 등록 간 동일한 장바구니 목록이 있는지 확인하는 메서드 ============ //
    private boolean checkAndIncreaseMatchingCartMenuCount(CreateCartRequestDto createCartRequestDto, List<CartMenu> cartMenus, Map<Long, List<CartMenuOption>> cartMenuIdToOptionsMap) {
        List<Long> options = createCartRequestDto.getOptions();
        Collections.sort(options);
        for (CartMenu cartMenu : cartMenus) {
            List<Long> optionIds = cartMenuIdToOptionsMap.get(cartMenu.getId()).stream()
                    .map(oi -> oi.getOption().getId())
                    .toList();
            if (optionIds.equals(options)) {
                cartMenu.increaseCount(createCartRequestDto.getCount());
                return true;
            }
        }
        return false;
    }

    // ============ 장바구니 등록 간 장바구니 목록과 장바구니 메뉴의 옵션 저장 메서드 ============ //
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

    // ============ 전체조회 간 장바구니 목록 Dto 반환 메서드 ============ //
    private List<CartMenuResponseDto> getCartMenuResponseDtos(Cart oneCart, Map<Long, List<CartMenuOption>> cartOptionMap) {
        List<CartMenuResponseDto> cartMenuResponseDtos = oneCart.getCartMenus().stream()
                .map(cartMenu-> {
                    List<CartMenuOptionResponseDto> optionDtos = cartOptionMap.get(cartMenu.getId())
                            .stream()
                            .map(CartMenuOptionResponseDto::new)
                            .collect(Collectors.toList());
                    CartMenuResponseDto cartMenuResponseDto = new CartMenuResponseDto(cartMenu, optionDtos);

                    return cartMenuResponseDto;
                })
                .collect(Collectors.toList());
        return cartMenuResponseDtos;
    }
}
