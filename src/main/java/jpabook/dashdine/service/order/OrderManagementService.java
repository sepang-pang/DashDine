package jpabook.dashdine.service.order;

import jpabook.dashdine.domain.cart.CartMenu;
import jpabook.dashdine.domain.cart.CartMenuOption;
import jpabook.dashdine.domain.order.*;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.order.CreateOrderParam;
import jpabook.dashdine.dto.response.menu.MenuFrom;
import jpabook.dashdine.dto.response.menu.OptionFrom;
import jpabook.dashdine.dto.response.order.OrderForm;
import jpabook.dashdine.repository.order.OrderRepository;
import jpabook.dashdine.service.cart.query.CartMenuOptionQueryService;
import jpabook.dashdine.service.cart.query.CartMenuQueryService;
import jpabook.dashdine.service.user.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderManagementService implements OrderService {

    private final OrderRepository orderRepository;
    private final UserInfoService userInfoService;
    private final CartMenuQueryService cartMenuQueryService;
    private final CartMenuOptionQueryService cartMenuOptionQueryService;

    @Override
    public void createOrder(User user, CreateOrderParam param) {
        // 엔티티 조회

        /*
        유저 조회
        */
        User findUser = userInfoService.findUser(user.getLoginId());

         /*
        장바구니 목록 조회
        */
        List<CartMenu> cartMenus = cartMenuQueryService.findCartMenus(param.getCartMenuIds());

         /*
        장바구니 각 목록에 매핑된 옵션 조회

        옵션을 조회할 때 목록의 in 절을 통해 목록의 Id 와 관련된 객체들을 한 번에 가져온다.
        이는 N + 1 문제를 방지하기 위함이다.
        이후 각 목록을 Key, 이에 매핑된 Option 들을 value 로 가지는 Map 을 생성한다.
        해당 Map 은 주문을 생성할 때 각 목록에 해당하는 option 들을 매핑할 때 사용된다.
        */
        Map<CartMenu, List<CartMenuOption>> cartMenuOptionsMap = getCartMenuOptionsMap(param.getCartMenuIds());

        // 배송정보 생성
        Delivery delivery = Delivery.builder()
                .address(user.getAddress())
                .deliveryStatus(DeliveryStatus.PENDING)
                .build();

        // 주문 상품 생성
        List<OrderMenu> orderMenu = OrderMenu.createOrderItem(cartMenus, cartMenuOptionsMap);

        // 주문 생성
        Order order = Order.createOrder(findUser, delivery, orderMenu);

        orderRepository.save(order);

        /*
        장바구니 비우기

        각 요소마다 delete 쿼리가 발생하는 현상을 방지하기 위해
        먼저 cart_menu_option 을 삭제 후 cart_menu 를 삭제한다.
        JPQL 을 이용하여 cart_menu 삭제 시 이와 연관된 cart_menu_option 들을 한 번에 지우는 것에는 한계가 있어, 이와 같이 구성했다.
        */
        emptyCart(cartMenus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderForm> readAllOrder(User user) {

        // 주문 조회;
        List<Order> findOrders = findAllOrders(user);


        // 주문 Dto 변환
        List<OrderForm> orderForms = findOrders.stream()
                .map(order -> {
                    Delivery delivery = order.getDelivery();
                    OrderForm orderForm = new OrderForm(order, delivery);

                    List<MenuFrom> menuFroms = order.getOrderMenus().stream()
                            .map(orderMenu -> {
                                MenuFrom menuFrom = new MenuFrom(orderMenu.getMenu(), orderMenu.getOrderPrice());

                                List<OptionFrom> optionFroms = orderMenu.getOrderMenuOptions().stream()
                                        .map(orderMenuOption -> new OptionFrom(orderMenuOption.getOption()))
                                        .toList();

                                menuFrom.setOptions(optionFroms);
                                return menuFrom;
                            })
                            .collect(Collectors.toList());

                    orderForm.setMenus(menuFroms);

                    return orderForm;
                })
                .toList();


        return orderForms;
    }

    private List<Order> findAllOrders(User user) {
        List<Order> orders = orderRepository.findAllOrdersWithDelivery(user.getId());
        if (orders == null || orders.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 항목입니다.");
        }
        return orders;
    }

    private Map<CartMenu, List<CartMenuOption>> getCartMenuOptionsMap(List<Long> cartMenuIds) {
        List<CartMenuOption> cartMenuOptions = cartMenuOptionQueryService.findCartOptionsByIds(cartMenuIds);

        if(cartMenuOptions.isEmpty()) {
            return null;
        }

        return cartMenuOptions.stream()
                .collect(Collectors.groupingBy(CartMenuOption::getCartMenu));
    }

    private void emptyCart(List<CartMenu> cartMenus) {
        cartMenuOptionQueryService.deleteAllCartMenuOptions(cartMenus);
        cartMenuQueryService.deleteCartMenus(cartMenus);
    }
}
