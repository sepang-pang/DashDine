package jpabook.dashdine.dto.request.menu;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateOptionRequestDto {

    private Long menuId;
    private String content;
    private int price;

    public CreateOptionRequestDto(Long menuId, String content, int price) {
        this.menuId = menuId;
        this.content = content;
        this.price = price;
    }
}
