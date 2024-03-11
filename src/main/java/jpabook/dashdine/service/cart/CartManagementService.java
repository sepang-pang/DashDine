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

        List<Long> cartMenuOptionIds = findCartMenu.getCartMenuOptions().stream()
                .map(cmo -> cmo.getOption().getId()).toList();

       /*
       요청한 옵션에서 기존 cart menu option 에 존재하지 않다면, cart menu option 에서 제거

       만약에 cart menu option 에 1, 2, 3, 4 의 값을 가지는 option 이 존재하고,
       요청 dto 는 2, 3, 4, 5 의 옵션을 가지고 있다면,
       사용자 측에서 기존 option 에서 " 1 " 은 취소하고, " 5 " 만 추가하길 바란다는 것으로 인지
       이에 장바구니 메뉴 옵션을 담는 DB 에서 해당 option 은 제거한다.
       */

        findCartMenu.getCartMenuOptions().removeIf(cartMenuOption ->
                !updateCartRequestDto.getOptions().contains(cartMenuOption.getOption().getId()));

        /*
        요청한 옵션에서 기존 cart menu option 에 값이 존재한다면, 요청 request dto 에서 해당 값은 제거

        만약에 cart menu option 에 2, 3, 4 가 존재하고 ( 1은 위에 단계에서 지워진 상태다. )
        request dto 는 2, 3, 4, 5 를 요청했다면,
        cart menu option 과 사용자가 요청한 옵션이 2, 3, 4 가 겹치니 이는 수정할 필요가 없다고 판단
        요청 dto 에서 해당 2, 3, 4 값을 제거하고, 최종적으로 option 5 만 새로 추가하고자 하는 것으로 판단한다.
        */

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
                .map(cartMenu -> {
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
