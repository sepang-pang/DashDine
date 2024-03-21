package jpabook.dashdine.dto.response.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.dashdine.domain.menu.Menu;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuForm {
    @JsonIgnore
    private Long menuId;
    private String name;
    private int price;
    private String content;
    private String image;
    private int stackQuantity;

    public MenuForm(Menu menu) {
        this.menuId = menu.getId();
        this.name = menu.getName();
        this.price = menu.getPrice();
        this.content = menu.getContent();
        this.image = menu.getImage();
        this.stackQuantity = menu.getStackQuantity();
    }

    public MenuForm(Long menuId, String name, int price, String content, String image, int stackQuantity) {
        this.menuId = menuId;
        this.name = name;
        this.price = price;
        this.content = content;
        this.image = image;
        this.stackQuantity = stackQuantity;
    }
}
