package jpabook.dashdine.dto.request.menu;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateMenuRequestDto {
    private String name;
    private Integer price;
    private String content;
    private String image;
    private Integer stackQuantity;

    public UpdateMenuRequestDto(String name, int price, String content, String image, int stackQuantity) {
        this.name = name;
        this.price = price;
        this.content = content;
        this.image = image;
        this.stackQuantity = stackQuantity;
    }
}
