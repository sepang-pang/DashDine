package jpabook.dashdine.service.order;

import jpabook.dashdine.domain.cart.CartMenu;
import jpabook.dashdine.domain.order.Order;
import jpabook.dashdine.domain.order.OrderMenu;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.repository.order.OrderRepository;
import jpabook.dashdine.service.cart.query.CartMenuQueryService;
import jpabook.dashdine.service.user.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j(topic = "Order Management Service Log")
public class OrderManagementService {

    private final OrderRepository orderRepository;
    private final UserInfoService userInfoService;
    private final CartMenuQueryService cartMenuQueryService;

    public void createOrder(User user, List<Long> cartMenuIds) {
        // 엔티티 조회
        System.out.println("// ======= 유저 조회 ======= //");
        User findUser = userInfoService.findUser(user.getLoginId());

        List<CartMenu> cartMenus = cartMenuQueryService.findCartMenus(cartMenuIds);

        // 주문 상품 생성
        System.out.println("// ======= 주문 상품 생성 ======= //");
        List<OrderMenu> orderMenu = OrderMenu.createOrderItem(cartMenus);

        // 주문 생성
        System.out.println("// ======= 주문 생성 ======= //");
        Order order = Order.createOrder(findUser, orderMenu);

        System.out.println("// ======= 주문 저장 ======= //");
        orderRepository.save(order);

        // 장바구니 비우기
        System.out.println("// ======= 장바구니 비우기 ======= //");
        cartMenuQueryService.deleteAll(cartMenus);
    }
}
