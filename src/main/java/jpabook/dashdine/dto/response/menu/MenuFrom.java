package jpabook.dashdine.dto.response.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MenuFrom {
    @JsonIgnore
    private Long menuId;
    private String name;
    private int price;
    private String content;
    private String image;
    private List<OptionFrom> options;

    public MenuFrom(Long menuId, String name, int price, String content, String image) {
        this.menuId = menuId;
        this.name = name;
        this.price = price;
        this.content = content;
        this.image = image;
    }
}
