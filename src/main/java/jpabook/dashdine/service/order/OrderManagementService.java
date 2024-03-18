package jpabook.dashdine.service.order;

import jpabook.dashdine.domain.cart.CartMenu;
import jpabook.dashdine.domain.cart.CartMenuOption;
import jpabook.dashdine.domain.order.*;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.order.CancelOrderParam;
import jpabook.dashdine.dto.request.order.CreateOrderParam;
import jpabook.dashdine.dto.request.order.ReceiveOrderParam;
import jpabook.dashdine.dto.response.menu.MenuForm;
import jpabook.dashdine.dto.response.menu.OptionForm;
import jpabook.dashdine.dto.response.order.OrderForm;
import jpabook.dashdine.repository.order.OrderRepository;
import jpabook.dashdine.service.cart.query.CartMenuOptionQueryService;
import jpabook.dashdine.service.cart.query.CartMenuQueryService;
import jpabook.dashdine.service.user.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderManagementService implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderQueryService orderQueryService;
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

    // 전체 주문 조회
    @Override
    @Transactional(readOnly = true)
    public List<OrderForm> readAllOrder(User user, OrderStatus status) {
        // 주문 조회
        List<Order> findOrders = findAllOrders(user, status);

        // 주문 폼 생성
        List<OrderForm> orderForms = findOrders.stream()
                .map(order -> {
                    Delivery delivery = order.getDelivery();
                    return new OrderForm(order, delivery);
                }).collect(Collectors.toList());

        System.out.println("// ============ 주문 목록 조회 ============ //");
        Result result = getOrderMenuMap(orderForms);


        // 주문 옵션 폼 생성
        System.out.println("// ============ 주문 옵션 조회 ============ //");
        Map<Long, List<OptionForm>> orderOpionsMap = getOrderOpionsMap(result.menuForms);


        // 최종 Dto 매핑
        result.menuForms.forEach(mf -> mf.setOptions(orderOpionsMap.get(mf.getOrderMenuId())));
        orderForms.forEach(of -> of.setMenus(result.orderMenusMap.get(of.getOrderId())));


        return orderForms;
    }

    // 단일 주문 조회
    @Override
    @Transactional(readOnly = true)
    public OrderForm readOneOrder(User user, Long orderId) {
        // 주문 조회
        Order findOrder = findOneOrder(user, orderId);

        // 주문 폼 생성
        OrderForm orderForm = new OrderForm(findOrder, findOrder.getDelivery());

        // 주문 메뉴 폼 생성
        List<OrderMenu> findOrderMenus = orderQueryService.getOrderMenusById(orderId);

        List<MenuForm> menuForms = findOrderMenus.stream()
                .map(MenuForm::new)
                .toList();

        System.out.println("// ============ 주문 옵션 조회 ============ //");
        Map<Long, List<OptionForm>> orderOpionsMap = getOrderOpionsMap(menuForms);

        menuForms.forEach(mf -> mf.setOptions(orderOpionsMap.get(mf.getOrderMenuId())));
        orderForm.setMenus(menuForms);

        return orderForm;
    }

    @Override
    public void cancelOrder(User user, Long orderId, CancelOrderParam param) {
        Order findOrder = findOneOrder(user, orderId);

        if(findOrder.getOrderStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("접수 완료가 되었거나, 이미 취소가 된 상품입니다.");
        }

        findOrder.cancelOrder(param);
    }

    @Override
    public void receiveOrder(Long orderId, ReceiveOrderParam param) {
        // 주문 조회
        Order findOrder = findOrderById(orderId);

        // 주문 접수
        findOrder.receiveOrder(param.getEstimateTime());
    }



    // ==== 주문 생성 메서드 ==== //
    private Map<CartMenu, List<CartMenuOption>> getCartMenuOptionsMap(List<Long> cartMenuIds) {
        List<CartMenuOption> cartMenuOptions = cartMenuOptionQueryService.findCartOptionsByIds(cartMenuIds);

        if (cartMenuOptions.isEmpty()) {
            return null;
        }

        return cartMenuOptions.stream()
                .collect(Collectors.groupingBy(CartMenuOption::getCartMenu));
    }

    private void emptyCart(List<CartMenu> cartMenus) {
        cartMenuOptionQueryService.deleteAllCartMenuOptions(cartMenus);
        cartMenuQueryService.deleteCartMenus(cartMenus);
    }

    // ==== 장바구니 조회 메서드 ==== //
    private Order findOrderById(Long orderId) {
        return orderRepository.findOneOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));
    }

    private Order findOneOrder(User user, Long orderId) {
        return orderRepository.findOneOrder(user, orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));
    }

    private List<Order> findAllOrders(User user, OrderStatus orderStatus) {

       List<Order> orders = (orderStatus == null) ?
               orderRepository.findAllOrdersWithDelivery(user.getId()) :
               orderRepository.findAllOrdersByStatus(user.getId(), orderStatus);

        if (orders == null || orders.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 항목입니다.");
        }

        return orders;
    }

    // 주문 목록에서 주문 Id 추출
    private List<Long> findOrderIds(List<OrderForm> orderForms) {
        return orderForms.stream()
                .map(OrderForm::getOrderId)
                .toList();
    }

    // 주문 메뉴 목록에서 메뉴 Id 추출
    private List<Long> findOrderMenuIds(List<MenuForm> menuForms) {
        return menuForms.stream()
                .map(MenuForm::getOrderMenuId)
                .collect(Collectors.toList());
    }

    // ==== 공통 데이터 처리 메서드 ==== //
    // 주문 메뉴 정보 매핑 및 관련 데이터 준비
    private Result getOrderMenuMap(List<OrderForm> orderForms) {
        List<OrderMenu> findOrderMenus = orderQueryService.getOrderMenusByIdIn(findOrderIds(orderForms));

        List<MenuForm> menuForms = findOrderMenus.stream()
                .map(MenuForm::new)
                .toList();

        Map<Long, List<MenuForm>> orderMenusMap = menuForms.stream()
                .collect(Collectors.groupingBy(MenuForm::getOrderId));

        return new Result(menuForms, orderMenusMap);
    }

    private record Result(List<MenuForm> menuForms, Map<Long, List<MenuForm>> orderMenusMap) {
    }

    // 주문 옵션 정보 조회 및 매핑
    private Map<Long, List<OptionForm>> getOrderOpionsMap(List<MenuForm> menuForms) {
        List<OrderMenuOption> findOrderMenuOptions = orderQueryService.getOrderMenuOptions(findOrderMenuIds(menuForms));

        List<OptionForm> optionForms = findOrderMenuOptions.stream()
                .map(OptionForm::new)
                .toList();

        return optionForms.stream()
                .collect(Collectors.groupingBy(OptionForm::getOrderMenuId));
    }


}
