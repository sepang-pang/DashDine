package jpabook.dashdine.dto.response.cart;

import jpabook.dashdine.domain.cart.CartMenu;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CartMenuForm {

    private Long cartMenuId;
    private int count;
    private int menuTotalPrice;
    private String image;
    private String menuName;
    private int menuPrice;
    private List<CartOptionForm> options;

    public CartMenuForm(CartMenu cartMenu) {
        this.cartMenuId = cartMenu.getId();
        this.count = cartMenu.getCount();
        this.image = cartMenu.getMenu().getImage();
        this.menuName = cartMenu.getMenu().getName();
        this.menuPrice = cartMenu.getMenu().getPrice();
    }


    public void updateCartOption(List<CartOptionForm> cartMenuOptions) {
        this.options = cartMenuOptions;
        this.menuTotalPrice = calculateTotalMenuPrice();
    }

    private int calculateTotalMenuPrice() {
        int optionTotalPrice = options.stream()
                .mapToInt(CartOptionForm::getPrice)
                .sum();

        return count * (menuPrice + optionTotalPrice);
    }
}
