package jpabook.dashdine.controller.order;

import jpabook.dashdine.dto.response.order.OrderForm;
import jpabook.dashdine.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderCommonController {

    private final OrderService orderService;

    @GetMapping("/order/{orderId}")
    public OrderForm readOneOrder(@PathVariable("orderId")Long orderId) {
        return orderService.readOneOrder(orderId);
    }
}
