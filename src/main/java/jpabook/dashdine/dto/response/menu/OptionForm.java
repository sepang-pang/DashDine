package jpabook.dashdine.dto.response.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OptionForm {
    @JsonIgnore
    private Long menuId;
    private Long optionId;
    private String content;
    private int price;

    public OptionForm(Long menuId, Long optionId, String content, int price) {
        this.menuId = menuId;
        this.optionId = optionId;
        this.content = content;
        this.price = price;
    }
}
