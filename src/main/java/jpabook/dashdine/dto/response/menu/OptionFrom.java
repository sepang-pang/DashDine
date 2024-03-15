package jpabook.dashdine.dto.response.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OptionFrom {
    @JsonIgnore
    private Long menuId;
    private String content;
    private int price;

    public OptionFrom(Long menuId, String content, int price) {
        this.menuId = menuId;
        this.content = content;
        this.price = price;
    }
}
