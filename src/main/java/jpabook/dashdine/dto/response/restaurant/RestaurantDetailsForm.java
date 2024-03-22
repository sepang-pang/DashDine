package jpabook.dashdine.dto.response.restaurant;

import jpabook.dashdine.domain.restaurant.Restaurant;
import jpabook.dashdine.dto.response.menu.MenuForm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RestaurantDetailsForm {
    private String name;
    private String tel;
    private String info;
    private String openingTime;
    private String closingTime;
    private boolean isOperating;
    private String category;
    private List<MenuForm> menuForms;

    public RestaurantDetailsForm(String name, String tel, String info, String openingTime, String closingTime, boolean isOperating, String category) {
        this.name = name;
        this.tel = tel;
        this.info = info;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.isOperating = isOperating;
        this.category = category;
    }
}
