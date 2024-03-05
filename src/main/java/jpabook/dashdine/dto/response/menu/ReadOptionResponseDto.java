package jpabook.dashdine.dto.response.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadOptionResponseDto {
    @JsonIgnore
    private Long optionId;
    @JsonIgnore
    private Long menuId;
    private String content;
    private int price;

    public ReadOptionResponseDto(Long optionId, Long menuId, String content, int price) {
        this.optionId = optionId;
        this.menuId = menuId;
        this.content = content;
        this.price = price;
    }
}
