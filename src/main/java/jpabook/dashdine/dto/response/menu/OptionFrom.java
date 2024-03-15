package jpabook.dashdine.dto.response.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.dashdine.domain.menu.Option;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OptionFrom {

    @JsonIgnore
    private Long optionId;
    private String content;
    private int price;

    public OptionFrom(Option option) {
        this.optionId = option.getId();
        this.content = option.getContent();
        this.price = option.getPrice();
    }
}
