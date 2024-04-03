package jpabook.dashdine.service.cart;

import jpabook.dashdine.domain.cart.Cart;
import jpabook.dashdine.domain.cart.CartMenu;
import jpabook.dashdine.domain.cart.CartMenuOption;
import jpabook.dashdine.domain.menu.Menu;
import jpabook.dashdine.domain.menu.Option;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.cart.AddCartParam;
import jpabook.dashdine.dto.request.cart.UpdateCartParam;
import jpabook.dashdine.dto.response.cart.CartForm;
import jpabook.dashdine.dto.response.cart.CartMenuForm;
import jpabook.dashdine.dto.response.cart.CartOptionForm;
import jpabook.dashdine.repository.cart.CartRepository;
import jpabook.dashdine.service.cart.query.CartMenuOptionQueryService;
import jpabook.dashdine.service.cart.query.CartMenuQueryService;
import jpabook.dashdine.service.menu.query.MenuQueryService;
import jpabook.dashdine.service.menu.query.OptionQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Cart Management Service")
@Transactional
public class CartManagementService implements CartService {

    private final CartRepository cartRepository;
    private final MenuQueryService menuQueryService;
    private final OptionQueryService optionQueryService;
    private final CartMenuQueryService cartMenuQueryService;
    private final CartMenuOptionQueryService cartMenuOptionQueryService;

    // ========= 장바구니 추가 ========= //
    @Override
    public void addCart(User user, AddCartParam param) {

        // 장바구니 조회
        Cart findCart = findOneCart(user);

        // 장바구니 목록 조회
        List<CartMenu> findCartMenus = cartMenuQueryService.findAllCartMenus(findCart);

       /*
            검증 로직

            1. 장바구니에는 동일한 가게의 품목만 담을 수 있음, 만일 다른 가게라면 기존 장바구니 비우기
            2. 추가하려는 메뉴 및 옵션의 조합이 이미 존재한다면, 기존 메뉴에서 count 만 증가
        */
        if (!findCartMenus.isEmpty()) {
            if (validateCart(findCartMenus, param)) return;
        }

        // 메뉴 조회
        Menu findMenu = menuQueryService.findOneMenu(param.getMenuId());

        // 옵션 조회
        List<Option> findOptions = optionQueryService.findOptions(param.getOptions());

        CartMenu cartMenu = CartMenu.CreateCartMenu(findCart, findMenu, findOptions, param);

        cartMenuQueryService.saveCartMenu(cartMenu);
    }

    // ========= 장바구니 조회 ========= //
    @Override
    @Transactional(readOnly = true)
    public CartForm readAllCart(User user) {
        // 장바구니 조회
        System.out.println("// ==== 장바구니 조회 ==== //");
        Cart findCart = findOneCart(user);

        // 장바구니 폼 생성
        System.out.println("// ==== 장바구니 폼 생성 ==== //");
        CartForm cartForm = new CartForm(findCart);

        // 장바구니 목록 폼 생성
        System.out.println("// ==== 장바구니 목록 폼 생성 ==== //");
        List<CartMenuForm> cartMenusForm = getCartMenuForms(findCart);

        // 장바구니 옵션 폼 생성
        System.out.println("// ==== 장바구니 옵션 폼 생성 ==== //");
        Map<Long, List<CartOptionForm>> cartOptionsMap = getCartOptionsMap(cartMenusForm);

        // 최종 Dto 매핑
        System.out.println("// ==== 최종 Dto 매핑 ==== //");

        cartMenusForm.forEach(cartMenuForm -> {
            List<CartOptionForm> cartOptionForms = cartOptionsMap.get(cartMenuForm.getCartMenuId());
            if (cartOptionForms != null) {
                cartMenuForm.updateCartOption(cartOptionForms);
            }
        });
        cartForm.updateCartMenuForm(cartMenusForm);

        return cartForm;
    }

    // ========= 장바구니 수정 ========= //
    @Override
    public void updateCart(User user, Long cartMenuId, Long menuId, UpdateCartParam param) {

        // 변경하고자 하는 메뉴를 조회한다.
        Menu findMenu = menuQueryService.findOneMenu(menuId);

        List<CartMenu> findCartMenus = cartMenuQueryService.findAllCartMenus(user.getCart(), findMenu);

        /*
        앞서 생성했던 목록 리스트에서 우리가 수정하고자 하는 목록의 Id 를 지니는 객체를 뽑아낸다.
        */

        CartMenu findCartMenu = findCartMenus.stream()
                .filter(cartMenu -> cartMenu.getId().equals(cartMenuId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));

        /*
           장바구니 검증
        */

        if (validateCart(param, findCartMenus, findCartMenu)) return;

       /*

       요청한 옵션이 기존 cart menu option 에 존재하지 않다면, cart menu option 에서 제거

       만약에 cart menu option 에 1, 2, 3, 4 의 값을 가지는 option 이 존재하고,
       요청 dto 는 2, 3, 4, 5 의 옵션을 가지고 있다면,
       사용자 측에서 기존 option 에서 " 1 " 은 취소하고, " 2, 3, 4 " 는 유지,  " 5 " 는 추가하길 바란다는 것으로 인지
       이에 장바구니 메뉴 옵션을 담는 DB 에서 해당 option 은 제거한다.

       */

        findCartMenu.getCartMenuOptions().removeIf(cartMenuOption ->
                !param.getOptions().contains(cartMenuOption.getOption().getId()));

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

        param.getOptions().removeIf(cartMenuOptionIds::contains);

        findCartMenu.updateCount(param.getCount());

        List<Option> optionList = optionQueryService.findOptions(param.getOptions());

        List<CartMenuOption> cartMenuOptions = optionList.stream()
                .map(option -> CartMenuOption.builder()
                        .cartMenu(findCartMenu)
                        .option(option)
                        .build())
                .collect(Collectors.toList());
        cartMenuOptionQueryService.saveAllCartMenuOptions(cartMenuOptions);
    }

    @Override
    public void deleteCart(User user, Long cartMenuId) {

        CartMenu findCartMenu = cartMenuQueryService.findOneCartMenu(cartMenuId);

        if (!findCartMenu.getCart().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인의 장바구니만 접근할 수 있습니다.");
        }

        cartMenuQueryService.deleteCartMenu(findCartMenu);
    }


    // ============ 공용 메서드 ============ //
    public Cart findOneCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 존재하지 않습니다."));
    }

    private Map<Long, List<CartMenuOption>> getCartOptionMap(List<CartMenu> cartMenus) {
        List<Long> cartMenuIds = cartMenus.stream()
                .map(CartMenu::getId)
                .collect(Collectors.toList());

        return cartMenuOptionQueryService.findCartOptions(cartMenuIds)
                .stream()
                .collect(Collectors.groupingBy(cmo -> cmo.getCartMenu().getId()));
    }

    // ============ 장바구니 생성 ============ //
    /*
        1. 장바구니에는 동일한 가게의 음식만 담을 수 있음
        2. 요청 메뉴의 가게와 기존 장바구니에 담겨있던 메뉴의 가게와 다르면, 기존 장바구니 비우기
    */
    private boolean validateCart(List<CartMenu> findCartMenus, AddCartParam param) {
        System.out.println("// ========== 장바구니 검증 ========== //");
        for (CartMenu cartMenu : findCartMenus) {
            if (deleteIfRestaurantMismatch(findCartMenus, cartMenu, param))
                break;

            if (cartMenu.getMenu().getId().equals(param.getMenuId())) {
                Set<Long> optionIds = cartMenu.getCartMenuOptions().stream()
                        .map(cmo -> cmo.getOption().getId())
                        .collect(Collectors.toSet());

                if (optionIds.equals(param.getOptions()) || ((param.getOptions().isEmpty()) && optionIds.isEmpty())) {
                    cartMenu.increaseCount(param.getCount());
                    return true;
                }
            }
        }

        return false;
    }

    private boolean deleteIfRestaurantMismatch(List<CartMenu> findCartMenus, CartMenu findCartMenu, AddCartParam param) {
        if (!findCartMenu.getMenu().getRestaurant().getId().equals(param.getRestaurantId())) {
            emptyCart(findCartMenus);
            return true;
        }
        return false;
    }

    private void emptyCart(List<CartMenu> findCartMenus) {
        cartMenuOptionQueryService.deleteAllCartMenuOptions(findCartMenus);
        cartMenuQueryService.deleteCartMenus(findCartMenus);
    }

    // ============ 장바구니 조회 ============ //
    // 장바구니 목록 폼 생성
    private List<CartMenuForm> getCartMenuForms(Cart findCart) {
        List<CartMenu> findCartMenus = cartMenuQueryService.findAllCartMenus(findCart);

        return findCartMenus.stream()
                .map(CartMenuForm::new)
                .toList();
    }

    // 장바구니 옵션 Map 생성
    private Map<Long, List<CartOptionForm>> getCartOptionsMap(List<CartMenuForm> cartMenusForm) {
        List<CartMenuOption> findCartMenuOptions = cartMenuOptionQueryService.findCartOptions(getCartMenuIds(cartMenusForm));

        List<CartOptionForm> cartOptionForms = findCartMenuOptions.stream()
                .map(CartOptionForm::new)
                .toList();

        return cartOptionForms.stream()
                .collect(Collectors.groupingBy(CartOptionForm::getCartMenuId));
    }

    // 장바구니 목록 Id 추출
    private List<Long> getCartMenuIds(List<CartMenuForm> cartMenusForm) {
        return cartMenusForm.stream()
                .map(CartMenuForm::getCartMenuId)
                .toList();
    }

    // ============ 장바구니 수정 ============ //
    /*
      1. 장바구니에 있는 모든 CartMenu를 순회.

      2. 요청한 옵션의 조합이 현재 순회 중인 CartMenu의 옵션 조합과 일치하는지 검사.

      3. 현재 순회 중인 CartMenu가 파라미터로 전달된 특정 CartMenu와 동일한 경우,
         해당 CartMenu의 수량을 param의 수량으로 업데이트합니다.

      4. 현재 순회 중인 CartMenu가 파라미터로 전달된 특정 CartMenu와 다른 경우,
         현재 CartMenu의 수량을 증가시키고, 파라미터로 전달된 CartMenu는 삭제.
         이는 요청된 옵션이 다른 장바구니 항목과 일치하는 경우에 적용.
    */
    private boolean validateCart(UpdateCartParam param, List<CartMenu> findCartMenus, CartMenu findCartMenu) {
        for (CartMenu cartMenu : findCartMenus) {
            Set<Long> optionIds = cartMenu.getCartMenuOptions().stream()
                    .map(cmo -> cmo.getOption().getId())
                    .collect(Collectors.toSet());


            if (optionIds.equals(param.getOptions()) || (param.getOptions().isEmpty() && optionIds.isEmpty())) {
                if (cartMenu.getId().equals(findCartMenu.getId())) {
                    cartMenu.updateCount(param.getCount());
                } else {
                    cartMenu.increaseCount(param.getCount());
                    cartMenuQueryService.deleteCartMenu(findCartMenu);
                }
                return true;
            }
        }
        return false;
    }
}
