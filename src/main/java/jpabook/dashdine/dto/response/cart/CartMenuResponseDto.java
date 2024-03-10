package jpabook.dashdine.dto.response.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.dashdine.domain.cart.CartMenu;
import jpabook.dashdine.domain.menu.Menu;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CartMenuResponseDto {
    @JsonIgnore
    private Long menuId;
    private int count;
    private int totalPrice;
    private String image;
    private String menuName;
    private int menuPrice;
    private List<CartMenuOptionResponseDto> options;

    public CartMenuResponseDto(CartMenu cartMenu) {
        this.count = cartMenu.getCount();
        this.image = cartMenu.getMenu().getImage();
        this.menuName = cartMenu.getMenu().getName();
        this.menuPrice = cartMenu.getMenu().getPrice();
    }
}
