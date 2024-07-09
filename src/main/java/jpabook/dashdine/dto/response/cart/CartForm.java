package jpabook.dashdine.dto.response.cart;

import jpabook.dashdine.domain.cart.Cart;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CartForm {

    private String username;
    private int totalPrice;
    private List<CartMenuForm> cartMenuForms;

    public CartForm(Cart cart) {
        this.username = cart.getUser().getNickName();
    }

    public void updateCartMenuForm(List<CartMenuForm> cartMenuForms) {
        this.cartMenuForms = cartMenuForms;
        calculateTotalMenuPrice(cartMenuForms);
    }

    private void calculateTotalMenuPrice(List<CartMenuForm> cartMenuForms) {
        this.totalPrice = cartMenuForms.stream()
                .mapToInt(CartMenuForm::getMenuTotalPrice)
                .sum();
    }
}

