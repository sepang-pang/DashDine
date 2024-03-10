package jpabook.dashdine.dto.response.cart;

import jpabook.dashdine.domain.cart.Cart;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CartResponseDto {

    private String username;
    private int totalPrice;
    private List<CartMenuResponseDto> cartMenus;

    public CartResponseDto(Cart cart) {
        this.username = cart.getUser().getLoginId();
    }

    public void updateCartDto(List<CartMenuResponseDto> cartMenus) {
        this.cartMenus = cartMenus;
        calculateTotalMenuPrice(cartMenus);
    }

    private void calculateTotalMenuPrice(List<CartMenuResponseDto> cartMenus) {
        this.totalPrice = cartMenus.stream()
                .mapToInt(CartMenuResponseDto::getMenuTotalPrice)
                .sum();
    }
}

