package jpabook.dashdine.dto.response.menu;

import jpabook.dashdine.domain.menu.Menu;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateMenuResponseDto {
    private String name;
    private int price;
    private String content;
    private String image;
    private int stackQuantity;

    public UpdateMenuResponseDto(Menu menu) {
        this.name = menu.getName();
        this.price = menu.getPrice();
        this.content = menu.getContent();
        this.image = menu.getImage();
        this.stackQuantity = menu.getStackQuantity();
    }
}
