package jpabook.dashdine.dto.response.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private List<CartMenuResponseDto> cartMenus;

    public CartResponseDto(String username, List<CartMenuResponseDto> cartMenus) {
        this.username = username;
        this.cartMenus = cartMenus;
    }

    public CartResponseDto(Cart cart) {
        this.username = cart.getUser().getLoginId();
    }
}

