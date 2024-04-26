package jpabook.dashdine.service.restaurant;

import jpabook.dashdine.advice.custom.ResourceNotFoundException;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.restaurant.CreateRestaurantParam;
import jpabook.dashdine.dto.request.restaurant.RadiusCondition;
import jpabook.dashdine.dto.request.restaurant.UpdateRestaurantParam;
import jpabook.dashdine.dto.response.restaurant.RestaurantDetailsForm;
import jpabook.dashdine.dto.response.restaurant.RestaurantForm;
import org.locationtech.jts.io.ParseException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RestaurantService {
    // ==== 공용 서비스 ==== //
    // 가게 상세 조회
    RestaurantDetailsForm readOneRestaurant(Long restaurantId);

    // == 고객 메서드 == //
    // 카테고리 별 가게 조회
    @Transactional(readOnly = true)
    List<RestaurantForm> readAllRestaurant(User user, Long categoryId, RadiusCondition cond) throws ResourceNotFoundException;

    // ==== 사장 서비스 ==== //
    // 가게 생성
    void createRestaurant(User user, CreateRestaurantParam param) throws ParseException;

    // 모든 가게 조회
    List<RestaurantForm> readAllRestaurant(User user) throws ResourceNotFoundException;

    // 가게 수정
    RestaurantForm updateRestaurant(User user, Long restaurantId, UpdateRestaurantParam param);

    // 가게 삭제
    void deleteRestaurant(User user, Long restaurantId);
}
