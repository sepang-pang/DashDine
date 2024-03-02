package jpabook.dashdine.dto.response.restaurant;

import lombok.Getter;

@Getter
public class RestaurantResponseDto {
    private String name;
    private String tel;
    private String info;
    private String openingTime;
    private String closingTime;
    private boolean isOperating;

    public RestaurantResponseDto() {

    }

    public RestaurantResponseDto(String name, String tel, String info, String openingTime, String closingTime, boolean isOperating) {
        this.name = name;
        this.tel = tel;
        this.info = info;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.isOperating = isOperating;
    }
}
