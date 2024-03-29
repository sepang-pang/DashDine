package jpabook.dashdine.dto.response.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.dashdine.domain.order.OrderMenu;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewMenuForm {
    @JsonIgnore
    private Long orderId;
    @JsonIgnore
    private Long menuId;
    private String menuName;

    public ReviewMenuForm(OrderMenu orderMenu) {
        this.orderId = orderMenu.getOrder().getId();
        this.menuId = orderMenu.getMenu().getId();
        this.menuName = orderMenu.getMenu().getName();
    }
}
