package jpabook.dashdine.dto.response.menu;

import jpabook.dashdine.domain.menu.Option;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OptionResponseDto {
    private String content;
    private int price;

    public OptionResponseDto(Option option) {
        this.content = option.getContent();
        this.price = option.getPrice();
    }
}
