package jpabook.dashdine.dto.response.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.dashdine.domain.common.Address;
import jpabook.dashdine.domain.order.Delivery;
import jpabook.dashdine.domain.order.Order;
import jpabook.dashdine.dto.response.menu.MenuFrom;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderForm {
    @JsonIgnore
    private Long orderId;

    // 주문 정보
    private int totalPrice;
    private String orderStatus;
    private LocalDateTime createdAt;
    private String cancelReason;
    private LocalDateTime deletedAt;

    // 배송 정보
    private String deliveryStatus;
    private LocalDateTime arrivedAt;
    private String city;
    private String street;
    private String zipcode;

    // 메뉴
    private List<MenuFrom> menus;

    public OrderForm(Order order, Delivery delivery) {

        this.orderId = order.getId();
        this.totalPrice = order.getTotalPrice();
        this.orderStatus = order.getOrderStatus().getStatus();
        this.createdAt = order.getCreatedAt();
        this.cancelReason = order.getCancelContent();
        this.deletedAt = order.getDeletedAt();

        this.deliveryStatus = delivery.getDeliveryStatus().getStatus();
        this.arrivedAt = delivery.getArrivedAt();
        this.city = delivery.getAddress().getCity();
        this.street = delivery.getAddress().getStreet();
        this.zipcode = delivery.getAddress().getZipcode();
    }
}
