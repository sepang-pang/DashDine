package jpabook.dashdine.dto.response.order;

import jpabook.dashdine.dto.response.menu.MenuFrom;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class OrderForm {
    private int totalPrice;
    private String orderStatus;
    private LocalDateTime createdAt;
    private String cancelReason;
    private LocalDateTime deletedAt;
    private List<MenuFrom> menus;

    public OrderForm(int totalPrice, String orderStatus, LocalDateTime createdAt, String cancelReason, LocalDateTime deletedAt) {
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.cancelReason = cancelReason;
        this.deletedAt = deletedAt;
    }
}
