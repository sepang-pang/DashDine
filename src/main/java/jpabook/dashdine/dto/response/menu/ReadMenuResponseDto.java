package jpabook.dashdine.dto.response.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReadMenuResponseDto {
    @JsonIgnore
    private Long menuId;
    private String name;
    private int price;
    private String content;
    private String image;
    private int stackQuantity;
    private List<ReadOptionResponseDto> options;


    public ReadMenuResponseDto(Long menuId, String name, int price, String content, String image, int stackQuantity) {
        this.menuId = menuId;
        this.name = name;
        this.price = price;
        this.content = content;
        this.image = image;
        this.stackQuantity = stackQuantity;
    }
}
