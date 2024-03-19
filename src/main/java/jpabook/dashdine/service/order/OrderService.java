package jpabook.dashdine.service.order;

import jpabook.dashdine.domain.order.DeliveryStatus;
import jpabook.dashdine.domain.order.OrderStatus;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.order.CancelOrderParam;
import jpabook.dashdine.dto.request.order.CreateOrderParam;
import jpabook.dashdine.dto.request.order.ReceiveOrderParam;
import jpabook.dashdine.dto.response.order.OrderForm;

import java.util.List;

public interface OrderService {

    // 공동 메서드
    // == 주문 단건 조회 == //
    OrderForm readOneOrder(Long orderId);

    // == 고객 메서드 == //
    // 주문 생성
    void createOrder(User user, CreateOrderParam param);

    // 본인 모든 주문 목록 조회
    List<OrderForm> readAllOrder(User user, OrderStatus orderStatus);

    // 본인 주문 취소
    void cancelOrder(User user, Long orderId, CancelOrderParam param);

    // == 사장 메서드 == //
    // 본인 가게 모든 주문 조회
    List<OrderForm> readAllOrderToOwner(User user, OrderStatus orderStatus);

    // 주문 접수
    void receiveOrder(Long orderId, ReceiveOrderParam param);

    // 배달
    void updateDelivery(Long orderId, DeliveryStatus deliveryStatus);
}
