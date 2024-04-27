package jpabook.dashdine.service.restaurant;

import jpabook.dashdine.advice.custom.ResourceNotFoundException;
import jpabook.dashdine.domain.restaurant.Category;
import jpabook.dashdine.domain.restaurant.Restaurant;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.restaurant.CreateRestaurantParam;
import jpabook.dashdine.dto.request.restaurant.RadiusCondition;
import jpabook.dashdine.dto.request.restaurant.UpdateRestaurantParam;
import jpabook.dashdine.dto.response.menu.MenuForm;
import jpabook.dashdine.dto.response.restaurant.RestaurantDetailsForm;
import jpabook.dashdine.dto.response.restaurant.RestaurantForm;
import jpabook.dashdine.repository.restaurant.RestaurantRepository;
import jpabook.dashdine.service.menu.query.MenuQueryService;
import jpabook.dashdine.service.restaurant.query.CategoryQueryService;
import jpabook.dashdine.service.user.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.io.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j(topic = "Restaurant Management Service Log")
public class RestaurantManagementService implements RestaurantService{

    private final RestaurantRepository restaurantRepository;
    private final UserQueryService userQueryService;
    private final CategoryQueryService categoryQueryService;
    private final MenuQueryService menuQueryService;

    // == 공용 메서드 == //
    // 가게 상세 조회
    @Transactional(readOnly = true)
    @Override
    public RestaurantDetailsForm readOneRestaurant(Long restaurantId) {

        RestaurantDetailsForm restaurantDetailsForm = restaurantRepository.findRestaurantDetailsFormById(restaurantId);

        if (restaurantDetailsForm == null) {
            throw new IllegalArgumentException("존재하지 않는 항목입니다.");
        }

        List<MenuForm> menuForms = menuQueryService.findAllMenuForms(restaurantId);

        restaurantDetailsForm.setMenuForms(menuForms);

        return restaurantDetailsForm;
    }

    // == 고객 메서드 == //
    // 카테고리 별 가게 조회
    @Transactional(readOnly = true)
    @Override
    public List<RestaurantForm> readAllRestaurant(User user, Long categoryId, RadiusCondition cond) throws ResourceNotFoundException {

        List<Restaurant> restaurants = restaurantRepository.findRestaurantsByCategoryId(user.getPoint(), cond.getRadius(), categoryId);

        List<RestaurantForm> result = restaurants.stream()
                .map(RestaurantForm::new)
                .collect(Collectors.toList());

        return validateAndReturn(result);
    }


    // == 사장 메서드 == //
    // 가게 등록
    @Override
    public void createRestaurant(User user, CreateRestaurantParam param) throws ParseException {
        // 유저 조회
        User findUser = userQueryService.findUser(user.getLoginId());

        // 본인이 소유한 가게 중 동일한 이름이 있을 경우 예외 발생
        checkForDuplicateRestaurantName(param.getName(), findUser);

        // 카테고리 조회
        Category category = getCategory(param.categoryId);

        // 가게 생성
        Restaurant restaurant = Restaurant.createRestaurant(findUser, param, category);

        restaurantRepository.save(restaurant);
    }

    // 보유한 모든 가게 조회
    @Transactional(readOnly = true)
    @Override
    public List<RestaurantForm> readAllRestaurant(User user) throws ResourceNotFoundException {
        List<RestaurantForm> findRestaurants = restaurantRepository.findRestaurantFormsByUserId(user.getId());

        return validateAndReturn(findRestaurants);
    }


    // 보유한 가게 수정
    @Override
    public RestaurantForm updateRestaurant(User user, Long restaurantId, UpdateRestaurantParam param) {
        // 가게 이름 검증
        checkForDuplicateRestaurantName(param.getName(), user);

        // 가게 조회
        Restaurant restaurant = findOneRestaurant(user, restaurantId);

        // 내용 수정
        restaurant.updateRestaurant(param, getCategory(param.getCategoryId()));

        return new RestaurantForm(restaurant);
    }

    // 가게 삭제
    @Override
    public void deleteRestaurant(User user, Long restaurantId) {
        Restaurant restaurant = findOneRestaurant(user, restaurantId);
        restaurant.deleteRestaurant();
    }


    // == 조회 메서드 == //
    // 본인 가게 조회
    public Restaurant findOneRestaurant(User user, Long restaurantId) {
        return restaurantRepository.findByUserIdAndIdAndIsDeletedFalse(user.getId(), restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 본인 소유의 가게가 아닙니다"));
    }


    // 카테고리 조회
    private Category getCategory(Long categoryId) {
        if (categoryId != null) {
            return categoryQueryService.findOneCategory(categoryId);
        } else {
            return null;
        }
    }

    // == 검증 메서드 == //
    // 가게 리스트 null 체크
    private List<RestaurantForm> validateAndReturn(List<RestaurantForm> restaurantForms) throws ResourceNotFoundException {
        if (restaurantForms.isEmpty()) {
            throw new ResourceNotFoundException("존재하지 않는 항목입니다.");
        }
        return restaurantForms;
    }

    // 본인 가게 중 중복 이름 검증 메서드
    private void checkForDuplicateRestaurantName(String requestName, User findUser) {
        List<String> findRestaurantNames = restaurantRepository.findRestaurantNameByUserId(findUser.getId());
        if (findRestaurantNames.contains(requestName)) {
            throw new IllegalArgumentException("이미 동일한 이름의 가게를 보유중입니다.");
        }
    }
}
