package jpabook.dashdine.dto.response.cart;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartMenuOptionResponseDto {
    private Long menuId;
    private Long optionId;

    public CartMenuOptionResponseDto(Long menuId, Long optionId) {
        this.menuId = menuId;
        this.optionId = optionId;
    }
}
