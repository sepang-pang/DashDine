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
import java.util.Optional;
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

    // ========= 장바구니 추가 ========= //
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
        if (checkAndIncreaseMatchingCartMenuCount(createCartRequestDto.getOptions(), createCartRequestDto.getCount(), cartMenus, cartMenuIdToOptionsMap))return;

        saveCartMenuAndCartOptions(createCartRequestDto, cart, menu, options);
    }

    // ========= 장바구니 조회 ========= //
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

    // ========= 장바구니 수정 ========= //
    public void updateCart(User user, Long cartMenuId, Long menuId, UpdateCartRequestDto updateCartRequestDto) {

        // 변경하고자 하는 메뉴를 조회한다.
        Menu findMenu = menuManagementService.findOneMenu(menuId);

        /*
        유저의 장바구니 정보와 앞서 조회했던 메뉴를 이용하여, 해당 메뉴가 존재하는 목록들을 모두 조회한다.
        목록들을 모두 조회하였으면, 목록의 Id 를 Key / 지니고 있는 option 들을 value 로 가지는 map 을 생성한다.
        이는 추후 중복 메뉴 검증때 사용된다.
        */
        List<CartMenu> findCartMenus = cartMenuQueryService.findCartMenusByCartAndMenu(user.getCart(), findMenu);

        Map<Long, List<CartMenuOption>> cartOptionMap = findCartOptionMap(findCartMenus);

        /*
        앞서 생성했던 목록 리스트에서 우리가 수정하고자 하는 목록의 Id 를 지니는 객체를 뽑아낸다.
        */
        Optional<CartMenu> result = findCartMenus.stream()
                .filter(cartMenu -> cartMenu.getId().equals(cartMenuId) && !cartMenu.isDeleted)
                .findFirst();

        CartMenu findCartMenu = result.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));

        if (!findCartMenu.getCart().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인 장바구니가 아닙니다.");
        }

       /*

       요청한 옵션이 기존 cart menu option 에 존재하지 않다면, cart menu option 에서 제거

       만약에 cart menu option 에 1, 2, 3, 4 의 값을 가지는 option 이 존재하고,
       요청 dto 는 2, 3, 4, 5 의 옵션을 가지고 있다면,
       사용자 측에서 기존 option 에서 " 1 " 은 취소하고, " 2, 3, 4 " 는 유지,  " 5 " 는 추가하길 바란다는 것으로 인지
       이에 장바구니 메뉴 옵션을 담는 DB 에서 해당 option 은 제거한다.

       */

        findCartMenu.getCartMenuOptions().removeIf(cartMenuOption ->
                !updateCartRequestDto.getOptions().contains(cartMenuOption.getOption().getId()));

        /*
        요청한 메뉴와 옵션이 기존 장바구니에 존재한다면 개수만 증가

        1. 현재 수정하고자 하는 Cart menu 의 menu 아이디를 가지는 Cart Menu 를 모두 조회
        2. 해당 Cart Menu 를 Cart Menu 의 Id 를 key, option Id 를 Value 로 가지는 Map 생성
        3. 현재 Request Dto 의 options 를 이용하여 비교
        4. 만약에 옵션 구성이 동일하다면 개수 증가, 아니면 아래 로직 수행

        */

        if (extracted(cartMenuId, updateCartRequestDto, findCartMenus, cartOptionMap, findCartMenu)) return;


        /*

        요청한 옵션에서 기존 cart menu option 에 값이 존재한다면, 요청 request dto 에서 해당 값은 제거

        만약에 cart menu option 에 2, 3, 4 가 존재하고 ( 1은 위에 단계에서 지워진 상태다. )
        request dto 는 2, 3, 4, 5 를 요청했다면,
        cart menu option 과 사용자가 요청한 옵션이 2, 3, 4 가 겹치니 이는 수정할 필요가 없다고 판단
        요청 dto 에서 해당 2, 3, 4 값을 제거하고, 최종적으로 option 5 만 새로 추가하고자 하는 것으로 판단한다.

        */

        // 기존 메뉴에 존재하던 option 의 Id 를 List 에 저장
        List<Long> cartMenuOptionIds = findCartMenu.getCartMenuOptions().stream()
                .map(cmo -> cmo.getOption().getId()).toList();

        updateCartRequestDto.getOptions().removeIf(cartMenuOptionIds::contains);

        findCartMenu.updateCount(updateCartRequestDto.getCount());

        List<Option> optionList = optionManagementService.findOptions(updateCartRequestDto.getOptions());

        List<CartMenuOption> cartMenuOptions = optionList.stream()
                .map(option -> CartMenuOption.builder()
                        .cartMenu(findCartMenu)
                        .option(option)
                        .build())
                .collect(Collectors.toList());
        cartMenuOptionQueryService.saveAllCartMenuOption(cartMenuOptions);
    }

    private boolean extracted(Long cartMenuId, UpdateCartRequestDto updateCartRequestDto, List<CartMenu> findCartMenus, Map<Long, List<CartMenuOption>> cartOptionMap, CartMenu findCartMenu) {
        Collections.sort(updateCartRequestDto.getOptions());
        for(CartMenu cartMenu : findCartMenus) {
            List<Long> optionIds = cartOptionMap.get(cartMenu.getId()).stream()
                    .map(oi -> oi.getOption().getId())
                    .toList();

            if(optionIds.equals(updateCartRequestDto.getOptions())) {
                if(cartMenu.getId().equals(cartMenuId)) {
                    cartMenu.updateCount(updateCartRequestDto.getCount());
                    return true;
                }
                cartMenu.increaseCount(updateCartRequestDto.getCount());
                cartMenuQueryService.deleteCartMenu(findCartMenu);
                return true;
            }
        }
        return false;
    }

    public void deleteCart(User user, Long cartMenuId) {

        CartMenu findCartMenu = cartMenuQueryService.findOneCartMenu(cartMenuId);

        if (!findCartMenu.getCart().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인의 장바구니만 접근할 수 있습니다.");
        }

        System.out.println("// ====== 장바구니 삭제 ======= //");
        findCartMenu.deleteCartMenu();
    }


    // ============ Private 메서드 ============ //
    private Cart findOneCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 존재하지 않습니다."));
    }

    private Map<Long, List<CartMenuOption>> findCartOptionMap(List<CartMenu> cartMenus) {
        // Cart Menu Id List 저장
        List<Long> cartMenuIds = cartMenus.stream()
                .filter(cartMenu -> !cartMenu.isDeleted)
                .map(CartMenu::getId)
                .collect(Collectors.toList());

        // Cart Menu Id 를 Key, Option Id 를 Value 로 가지는 Map 생성
        // 이때 Option Fetch Join
        System.out.println("// ======= 장바구니 옵션 조회 ======= //");
        return cartMenuOptionQueryService.findCartOptionsByIds(cartMenuIds)
                .stream()
                .collect(Collectors.groupingBy(cmo -> cmo.getCartMenu().getId()));
    }

    // ============ 전체조회 등록 간 동일한 장바구니 목록이 있는지 확인하는 메서드 ============ //
    private boolean checkAndIncreaseMatchingCartMenuCount(List<Long> options, int count, List<CartMenu> cartMenus, Map<Long, List<CartMenuOption>> cartMenuIdToOptionsMap) {
        Collections.sort(options);
        for (CartMenu cartMenu : cartMenus) {
            List<Long> optionIds = cartMenuIdToOptionsMap.get(cartMenu.getId()).stream()
                    .map(oi -> oi.getOption().getId())
                    .toList();
            if (optionIds.equals(options)) {
                cartMenu.increaseCount(count);
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
                .filter(cartMenu -> !cartMenu.isDeleted)
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
