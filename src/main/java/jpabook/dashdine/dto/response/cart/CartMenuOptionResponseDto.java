package jpabook.dashdine.dto.response.cart;

import jpabook.dashdine.domain.cart.CartMenuOption;
import jpabook.dashdine.domain.menu.Option;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartMenuOptionResponseDto {
    private String content;
    private int price;

    public CartMenuOptionResponseDto(Option option) {
        this.content = option.getContent();
        this.price = option.getPrice();
    }

    public CartMenuOptionResponseDto(CartMenuOption cartMenuOption) {
        this.content = cartMenuOption.getOption().getContent();
        this.price = cartMenuOption.getOption().getPrice();
    }
}
