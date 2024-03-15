package jpabook.dashdine.dto.response.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.dashdine.domain.menu.Menu;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MenuFrom {
    @JsonIgnore
    private Long menuId;
    private String name;
    private int price;
    private String content;
    private String image;
    private List<OptionFrom> options;

    public MenuFrom(Menu menu, int price) {
        this.menuId = menu.getId();
        this.name = menu.getName();
        this.price = price;
        this.content = menu.getContent();
        this.image = menu.getImage();
    }
}
