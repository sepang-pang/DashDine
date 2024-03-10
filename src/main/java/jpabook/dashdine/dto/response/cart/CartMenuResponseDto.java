package jpabook.dashdine.dto.response.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.dashdine.domain.cart.CartMenu;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CartMenuResponseDto {

    private int count;
    private int menuTotalPrice;
    private String image;
    private String menuName;
    private int menuPrice;
    @JsonIgnore private int optionPrice;
    private List<CartMenuOptionResponseDto> options;

    public CartMenuResponseDto(CartMenu cartMenu, List<CartMenuOptionResponseDto> optionDtos) {
        this.count = cartMenu.getCount();
        this.image = cartMenu.getMenu().getImage();
        this.menuName = cartMenu.getMenu().getName();
        this.menuPrice = cartMenu.getMenu().getPrice();
        this.options = optionDtos;
        this.optionPrice += calculateTotalOptionPrice(optionDtos);
        this.menuTotalPrice = calculateTotalMenuPrice();
    }

    private int calculateTotalMenuPrice() {
        return count * (menuPrice + optionPrice);
    }

    private int calculateTotalOptionPrice(List<CartMenuOptionResponseDto> optionDtos) {
        return optionDtos.stream()
                .mapToInt(CartMenuOptionResponseDto::getPrice)
                .sum();
    }
}
