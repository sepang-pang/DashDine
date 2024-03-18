package jpabook.dashdine.service.order;

import jpabook.dashdine.domain.order.OrderStatus;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.order.CancelOrderParam;
import jpabook.dashdine.dto.request.order.CreateOrderParam;
import jpabook.dashdine.dto.response.order.OrderForm;

import java.util.List;

public interface OrderService {
    void createOrder(User user, CreateOrderParam param);
    List<OrderForm> readAllOrder(User user, OrderStatus orderStatus);
    OrderForm readOneOrder(User user, Long orderId);
    void cancelOrder(User user, Long orderId, CancelOrderParam param);
}
