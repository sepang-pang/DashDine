package jpabook.dashdine.dto.response.order;

import jpabook.dashdine.domain.order.OrderMenu;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderMenuForm {
    private Long orderId;
    private Long orderMenuId;
    private Long menuId;
    private String name;
    private int price;
    private String content;
    private String image;
    private List<OrderOptionForm> optionForms;

    public OrderMenuForm(OrderMenu orderMenu) {
        this.orderId = orderMenu.getOrder().getId();
        this.orderMenuId = orderMenu.getId();
        this.menuId = orderMenu.getMenu().getId();
        this.name = orderMenu.getMenu().getName();
        this.price = orderMenu.getOrderPrice();
        this.content = orderMenu.getMenu().getContent();
        this.image = orderMenu.getMenu().getImage();
    }
}
