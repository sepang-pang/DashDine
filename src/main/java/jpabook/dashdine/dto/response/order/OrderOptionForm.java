package jpabook.dashdine.dto.response.order;

import jpabook.dashdine.domain.order.OrderMenuOption;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderOptionForm {

    private Long optionId;
    private Long orderMenuId;
    private String content;
    private int price;

    public OrderOptionForm(OrderMenuOption orderMenuOption) {
        this.optionId = orderMenuOption.getOption().getId();
        this.orderMenuId = orderMenuOption.getOrderMenu().getId();
        this.content = orderMenuOption.getOption().getContent();
        this.price = orderMenuOption.getOptionPrice();
    }
}
