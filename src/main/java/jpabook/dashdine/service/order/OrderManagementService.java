package jpabook.dashdine.service.order;

import jpabook.dashdine.domain.cart.CartMenu;
import jpabook.dashdine.domain.cart.CartMenuOption;
import jpabook.dashdine.domain.order.*;
import jpabook.dashdine.domain.restaurant.Restaurant;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.order.CancelOrderParam;
import jpabook.dashdine.dto.request.order.CreateOrderParam;
import jpabook.dashdine.dto.request.order.ReceiveOrderParam;
import jpabook.dashdine.dto.response.order.OrderMenuForm;
import jpabook.dashdine.dto.response.order.OrderOptionForm;
import jpabook.dashdine.dto.response.order.OrderForm;
import jpabook.dashdine.repository.order.OrderRepository;
import jpabook.dashdine.service.cart.query.CartMenuOptionQueryService;
import jpabook.dashdine.service.cart.query.CartMenuQueryService;
import jpabook.dashdine.service.order.query.OrderMenuOptionQueryService;
import jpabook.dashdine.service.order.query.OrderMenuQueryService;
import jpabook.dashdine.service.restaurant.query.RestaurantQueryService;
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
    private final OrderMenuQueryService orderMenuQueryService;
    private final OrderMenuOptionQueryService orderMenuOptionQueryService;
    private final RestaurantQueryService restaurantQueryService;
    private final UserInfoService userInfoService;
    private final CartMenuQueryService cartMenuQueryService;
    private final CartMenuOptionQueryService cartMenuOptionQueryService;

    // 공동 메서드
    // == 주문 단건 조회 == //
    @Override
    @Transactional(readOnly = true)
    public OrderForm readOneOrder(Long orderId) {
        // 주문 조회
        Order findOrder = findOneOrder(orderId);

        // 주문 폼 생성
        OrderForm orderForm = new OrderForm(findOrder, findOrder.getDelivery());

        // 주문 메뉴 폼 생성
        List<OrderMenu> findOrderMenus = orderMenuQueryService.findOrderMenusById(orderId);

        List<OrderMenuForm> orderMenuForms = findOrderMenus.stream()
                .map(OrderMenuForm::new)
                .toList();

        Map<Long, List<OrderOptionForm>> orderOpionsMap = getOrderOpionsMap(orderMenuForms);

        orderMenuForms.forEach(mf -> mf.setOptionForms(orderOpionsMap.get(mf.getOrderMenuId())));
        orderForm.setMenuForms(orderMenuForms);

        return orderForm;
    }


    // == 고객 메서드 == //
    // 주문 생성
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

    // 본인 모든 주문 목록 조회
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

        Result result = getOrderMenuMap(orderForms);


        // 주문 옵션 폼 생성
        Map<Long, List<OrderOptionForm>> orderOpionsMap = getOrderOpionsMap(result.orderMenuForms);


        // 최종 Dto 매핑
        result.orderMenuForms.forEach(mf -> mf.setOptionForms(orderOpionsMap.get(mf.getOrderMenuId())));
        orderForms.forEach(of -> of.setMenuForms(result.orderMenusMap.get(of.getOrderId())));


        return orderForms;
    }

    // 본인 주문 취소
    @Override
    public void cancelOrder(User user, Long orderId, CancelOrderParam param) {
        Order findOrder = findOneOrder(orderId);

        checkOrderAccessPermission(user, findOrder);

        findOrder.cancelOrder(param);
    }

    // 주문 내역 삭제
    @Override
    public void deleteOrderDetails(User user, Long orderId) {
        Order findOrder = findOneOrder(orderId);

        checkOrderAccessPermission(user, findOrder);

        findOrder.deleteOrder();
    }

    // == 사장 메서드 == //
    // 본인 가게 모든 주문 조회
    @Override
    @Transactional(readOnly = true)
    public List<OrderForm> readAllOrderToOwner(User user, OrderStatus orderStatus) {
        // 본인 점포 조회
        List<Restaurant> findRestaurants = restaurantQueryService.findAllRestaurantByUserId(user.getId());

        List<Long> restaurantIds = findRestaurants.stream()
                .map(Restaurant::getId)
                .toList();

        // 점포에 따른 주문 목록 조회
        List<OrderMenu> findOrderMenus = orderMenuQueryService.findAllOrderMenusByRestaurantIds(restaurantIds);

        // 주문 폼 생성
        List<OrderForm> orderForms = getOrderForms(findOrderMenus, orderStatus);

        // 주문 목록 생성
        List<OrderMenuForm> orderMenuForms = findOrderMenus.stream()
                .map(OrderMenuForm::new)
                .toList();

        Map<Long, List<OrderMenuForm>> orderMenusMap = orderMenuForms.stream()
                .collect(Collectors.groupingBy(OrderMenuForm::getOrderId));

        // 주문 옵션 목록 생성
        Map<Long, List<OrderOptionForm>> orderOpionsMap = getOrderOpionsMap(orderMenuForms);

        // 최종 Dto 매핑
        orderMenuForms.forEach(mf -> mf.setOptionForms(orderOpionsMap.get(mf.getOrderMenuId())));
        orderForms.forEach(of -> of.setMenuForms(orderMenusMap.get(of.getOrderId())));

        return orderForms;
    }

    // 주문 접수
    @Override
    public void receiveOrder(Long orderId, ReceiveOrderParam param) {
        // 주문 조회
        Order findOrder = findOrderById(orderId);

        // 주문 접수
        findOrder.receiveOrder(param.getEstimateTime());
    }

    @Override
    public void updateDelivery(Long orderId, DeliveryStatus deliveryStatus) {
        // 주문 조회
        Order findOrder = findOneOrder(orderId);

        Delivery delivery = findOrder.getDelivery();

        delivery.updateDeliveryStatus(deliveryStatus);
    }

    // === 주문 조회 메서드 === //
    private List<OrderForm> getOrderForms(List<OrderMenu> findOrderMenus, OrderStatus orderStatus) {
        List<Long> orderIds = findOrderMenus.stream()
                .map(orderMenu -> orderMenu.getOrder().getId())
                .toList();

        List<Order> orders = (orderStatus == null) ?
                orderRepository.findAllOrdersByIdIn(orderIds) :
                orderRepository.findAllOrdersByIdInAndStatus(orderIds, orderStatus);


        if (orders == null || orders.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 항목입니다.");
        }

        return orders.stream()
                .map(order -> new OrderForm(order, order.getDelivery()))
                .toList();
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

    private Order findOneOrder(Long orderId) {
        return orderRepository.findOneOrder(orderId)
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

    // === Id 추출 메서드 === //
    // 주문 목록에서 주문 Id 추출
    private List<Long> findOrderIds(List<OrderForm> orderForms) {
        return orderForms.stream()
                .map(OrderForm::getOrderId)
                .toList();
    }

    // 주문 메뉴 목록에서 메뉴 Id 추출
    private List<Long> findOrderMenuIds(List<OrderMenuForm> orderMenuForms) {
        return orderMenuForms.stream()
                .map(OrderMenuForm::getOrderMenuId)
                .collect(Collectors.toList());
    }

    // ==== 공통 데이터 처리 메서드 ==== //
    // 본인 인증 메서드
    private void checkOrderAccessPermission(User user, Order findOrder) {
        if(!user.getId().equals(findOrder.getUser().getId())){
            throw new IllegalArgumentException("해당 주문에 대한 접근 권한이 없습니다.");
        }
    }

    // 주문 메뉴 정보 매핑 및 관련 데이터 준비
    private Result getOrderMenuMap(List<OrderForm> orderForms) {
        List<OrderMenu> findOrderMenus = orderMenuQueryService.findOrderMenusByIdIn(findOrderIds(orderForms));

        List<OrderMenuForm> orderMenuForms = findOrderMenus.stream()
                .map(OrderMenuForm::new)
                .toList();

        Map<Long, List<OrderMenuForm>> orderMenusMap = orderMenuForms.stream()
                .collect(Collectors.groupingBy(OrderMenuForm::getOrderId));

        return new Result(orderMenuForms, orderMenusMap);
    }

    private record Result(List<OrderMenuForm> orderMenuForms, Map<Long, List<OrderMenuForm>> orderMenusMap) {
    }

    // 주문 옵션 정보 조회 및 매핑
    private Map<Long, List<OrderOptionForm>> getOrderOpionsMap(List<OrderMenuForm> orderMenuForms) {
        List<OrderMenuOption> findOrderMenuOptions = orderMenuOptionQueryService.findOrderMenuOptionsByMenuIdIn(findOrderMenuIds(orderMenuForms));

        List<OrderOptionForm> orderOptionForms = findOrderMenuOptions.stream()
                .map(OrderOptionForm::new)
                .toList();

        return orderOptionForms.stream()
                .collect(Collectors.groupingBy(OrderOptionForm::getOrderMenuId));
    }


}
