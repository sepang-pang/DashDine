package jpabook.dashdine.service.order;

import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.order.CreateOrderParam;

public interface OrderService {
    public void createOrder(User user, CreateOrderParam param);
}
