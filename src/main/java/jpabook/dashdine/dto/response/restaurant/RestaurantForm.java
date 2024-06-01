package jpabook.dashdine.dto.response.restaurant;

import jpabook.dashdine.domain.restaurant.Restaurant;
import lombok.Getter;

@Getter
public class RestaurantForm {
    private Long restaurantId;
    private String name;
    private String tel;
    private String info;
    private String openingTime;
    private String closingTime;
    private int minimumPrice;
    private boolean isOperating;
    private String category;
    private Long categoryId;
    private String zipcode;
    private String street;
    private String streetDetail;

    public RestaurantForm() {

    }

    public RestaurantForm(Long restaurantId, String name, String tel, String info, String openingTime, String closingTime, boolean isOperating, int minimumPrice, String category, Long categoryId, String street, String streetDetail) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.tel = tel;
        this.info = info;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.isOperating = isOperating;
        this.minimumPrice = minimumPrice;
        this.category = category;
        this.categoryId = categoryId;
        this.street = street;
        this.streetDetail = streetDetail;
    }

    public RestaurantForm(Long restaurantId, String name, String tel, String info, String openingTime, String closingTime, boolean isOperating, int minimumPrice, String category, Long categoryId, String zipcode, String street, String streetDetail) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.tel = tel;
        this.info = info;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.isOperating = isOperating;
        this.minimumPrice = minimumPrice;
        this.category = category;
        this.categoryId = categoryId;
        this.zipcode = zipcode;
        this.street = street;
        this.streetDetail = streetDetail;
    }

    public RestaurantForm(Restaurant restaurant) {
        this.restaurantId = restaurant.getId();
        this.name = restaurant.getName();
        this.tel = restaurant.getTel();
        this.info = restaurant.getInfo();
        this.openingTime = restaurant.getOpeningTime();
        this.closingTime = restaurant.getClosingTime();
        this.minimumPrice = restaurant.getMinimumPrice();
        this.isOperating = restaurant.isOperating();
        this.category = restaurant.getCategory().getName();
        this.categoryId = restaurant.getCategory().getId();
        this.zipcode = restaurant.getAddress().getZipcode();
        this.street = restaurant.getAddress().getStreet();
        this.streetDetail = restaurant.getAddress().getStreetDetail();
    }
}
