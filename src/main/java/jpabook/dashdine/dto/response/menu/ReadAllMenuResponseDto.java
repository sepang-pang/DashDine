package jpabook.dashdine.dto.response.menu;

import jpabook.dashdine.domain.menu.Menu;
import jpabook.dashdine.domain.menu.Option;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ReadAllMenuResponseDto {
    private String name;
    private int price;
    private String content;
    private String image;
    private int stackQuantity;
    private List<OptionResponseDto> options;

    public ReadAllMenuResponseDto(Menu menu) {
        this.name = menu.getName();
        this.price = menu.getPrice();
        this.content = menu.getContent();
        this.image = menu.getImage();
        this.stackQuantity = menu.getStackQuantity();
        this.options = menu.getOptions().stream()
                .map(option -> new OptionResponseDto(option))
                .collect(Collectors.toList());
    }
}
