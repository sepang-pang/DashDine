package jpabook.dashdine.dto.response.cart;

import jpabook.dashdine.domain.cart.CartMenuOption;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartOptionForm {

    private Long cartMenuId;
    private String content;
    private int price;

    public CartOptionForm(CartMenuOption cartMenuOption) {
        this.cartMenuId = cartMenuOption.getCartMenu().getId();
        this.content = cartMenuOption.getOption().getContent();
        this.price = cartMenuOption.getOption().getPrice();
    }
}
