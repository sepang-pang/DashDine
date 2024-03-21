package jpabook.dashdine.service.restaurant;

import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.restaurant.CreateRestaurantParam;
import jpabook.dashdine.dto.request.restaurant.UpdateRestaurantParam;
import jpabook.dashdine.dto.response.restaurant.RestaurantDetailsForm;
import jpabook.dashdine.dto.response.restaurant.RestaurantForm;

import java.util.List;

public interface RestaurantService {

    // ==== 사장 서비스 ==== //
    // 가게 생성
    void createRestaurant(User user, CreateRestaurantParam param);

    // 모든 가게 조회
    List<RestaurantForm> readAllRestaurant(User user);

    // 가게 상세 조회
    RestaurantDetailsForm readOneRestaurant(Long restaurantId);

    // 가게 수정
    RestaurantForm updateRestaurant(User user, Long restaurantId, UpdateRestaurantParam param);

    // 가게 삭제
    void deleteRestaurant(User user, Long restaurantId);

    // ==== 고객 서비스 ==== //
    // 카테고리 별 가게 조회
    List<RestaurantForm> readAllRestaurant(Long categoryId);



}
