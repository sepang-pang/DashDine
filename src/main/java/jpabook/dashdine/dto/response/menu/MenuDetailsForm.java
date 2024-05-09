package jpabook.dashdine.dto.response.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MenuDetailsForm {
    private Long menuId;
    private Long restaurantId;
    private String name;
    private int price;
    private String content;
    private String image;
    private int stackQuantity;
    private List<OptionForm> options;


    public MenuDetailsForm(Long menuId, String name, int price, String content, String image, int stackQuantity, Long restaurantId) {
        this.menuId = menuId;
        this.restaurantId = restaurantId;
        this.name = name;
        this.price = price;
        this.content = content;
        this.image = image;
        this.stackQuantity = stackQuantity;
    }
}
