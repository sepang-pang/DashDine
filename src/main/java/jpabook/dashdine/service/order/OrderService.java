package jpabook.dashdine.service.order;

import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.order.CreateOrderParam;
import jpabook.dashdine.dto.response.order.OrderForm;

import java.util.List;

public interface OrderService {
    void createOrder(User user, CreateOrderParam param);
    List<OrderForm> readAllOrder(User user);
}
