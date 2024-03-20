package jpabook.dashdine.dto.response.order;

import jpabook.dashdine.domain.order.Delivery;
import jpabook.dashdine.domain.order.Order;
import jpabook.dashdine.dto.response.menu.OrderMenuForm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderForm {

    private Long orderId;

    // 주문 정보
    private int totalPrice;
    private String orderStatus;
    private LocalDateTime createdAt;
    private String cancelContent;
    private LocalDateTime deletedAt;

    // 배송 정보
    private String deliveryStatus;
    private LocalDateTime arrivedAt;
    private String city;
    private String street;
    private String zipcode;

    // 메뉴
    private List<OrderMenuForm> menus;

    public OrderForm(Order order, Delivery delivery) {

        this.orderId = order.getId();
        this.totalPrice = order.getTotalPrice();
        this.orderStatus = order.getOrderStatus().getStatus();
        this.createdAt = order.getCreatedAt();
        this.cancelContent = order.getCancelContent();
        this.deletedAt = order.getDeletedAt();

        this.deliveryStatus = delivery.getDeliveryStatus().getStatus();
        this.arrivedAt = delivery.getArrivedAt();
        this.city = delivery.getAddress().getCity();
        this.street = delivery.getAddress().getStreet();
        this.zipcode = delivery.getAddress().getZipcode();
    }
}
