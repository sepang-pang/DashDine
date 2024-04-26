package jpabook.dashdine.dto.response.restaurant;

import jpabook.dashdine.domain.common.Address;
import jpabook.dashdine.domain.restaurant.Restaurant;
import lombok.Getter;

@Getter
public class RestaurantForm {
    private String name;
    private String tel;
    private String info;
    private String openingTime;
    private String closingTime;
    private int minimumPrice;
    private boolean isOperating;
    private String category;
    private String street;
    private String streetDetail;

    public RestaurantForm() {

    }

    public RestaurantForm(String name, String tel, String info, String openingTime, String closingTime, boolean isOperating, int minimumPrice, String category, String street, String streetDetail) {
        this.name = name;
        this.tel = tel;
        this.info = info;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.isOperating = isOperating;
        this.minimumPrice = minimumPrice;
        this.category = category;
        this.street = street;
        this.streetDetail = streetDetail;
    }

    public RestaurantForm(Restaurant restaurant) {
        this.name = restaurant.getName();
        this.tel = restaurant.getTel();
        this.info = restaurant.getInfo();
        this.openingTime = restaurant.getOpeningTime();
        this.closingTime = restaurant.getClosingTime();
        this.category = restaurant.getCategory().getName();
    }
}
