package jpabook.dashdine.dto.response.restaurant;

import jpabook.dashdine.domain.restaurant.Restaurant;
import lombok.Getter;

@Getter
public class RestaurantResponseDto {
    private String name;
    private String tel;
    private String info;
    private String openingTime;
    private String closingTime;
    private boolean isOperating;
    private String category;

    public RestaurantResponseDto() {

    }

    public RestaurantResponseDto(String name, String tel, String info, String openingTime, String closingTime, boolean isOperating, String category) {
        this.name = name;
        this.tel = tel;
        this.info = info;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.isOperating = isOperating;
        this.category = category;
    }

    public RestaurantResponseDto(Restaurant restaurant) {
        this.name = restaurant.getName();
        this.tel = restaurant.getTel();
        this.info = restaurant.getInfo();
        this.openingTime = restaurant.getOpeningTime();
        this.closingTime = restaurant.getClosingTime();
        this.category = restaurant.getCategory().getName();
    }
}
