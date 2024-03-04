package jpabook.dashdine.dto.request.menu;

import lombok.Getter;

@Getter
public class CreateMenuRequestDto {
    private Long restaurantId;
    private String name;
    private int price;
    private String content;
    private String image;
    private int stackQuantity;
}
