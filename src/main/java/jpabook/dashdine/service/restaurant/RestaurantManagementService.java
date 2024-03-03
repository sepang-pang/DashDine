package jpabook.dashdine.service.restaurant;

import jpabook.dashdine.domain.restaurant.Restaurant;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.restaurant.CreateRestaurantDto;
import jpabook.dashdine.dto.request.restaurant.UpdateRestaurantRequestDto;
import jpabook.dashdine.dto.response.restaurant.RestaurantResponseDto;
import jpabook.dashdine.repository.restaurant.RestaurantRepository;
import jpabook.dashdine.service.user.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j(topic = "Restaurant Management Service Log")
public class RestaurantManagementService {

    private final RestaurantRepository restaurantRepository;
    private final UserInfoService userInfoService;

    // 가게 등록
    public void createRestaurant(User user, CreateRestaurantDto createRestaurantDto) {
        // 유저 조회
        System.out.println("// ========== Select Query ========== //");
        User findUser = userInfoService.findUser(user.getLoginId());

        // 본인이 소유한 가게 중 동일한 이름이 있을 경우 예외 발생
        System.out.println("// ========== Select Query ========== //");
        checkForDuplicateRestaurantName(createRestaurantDto, findUser);

        log.info("식당 생성");
        Restaurant restaurant = Restaurant.builder()
                .name(createRestaurantDto.getName())
                .info(createRestaurantDto.getInfo())
                .tel(createRestaurantDto.getTel())
                .minimumPrice(createRestaurantDto.getMinimumPrice())
                .openingTime(createRestaurantDto.getOpeningTime())
                .closingTime(createRestaurantDto.getClosingTime())
                .user(findUser)
                .build();

        System.out.println("// =========== Save =========== //");
        restaurantRepository.save(restaurant);
    }


    // 보유한 모든 가게 조회
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> readAllRestaurant(User user) {
        System.out.println("// ========== Select Query ========== //");
        return restaurantRepository.findRestaurantListByUserId(user.getId());
    }

    // 보유한 가게 선택 조회
    @Transactional(readOnly = true)
    public RestaurantResponseDto readRestaurant(User user, Long restaurantId) {
        System.out.println("// ========== Select Query ========== //");
        return restaurantRepository.findOneRestaurantByUserId(user.getId(), restaurantId);
    }

    // 보유한 가게 수정
    public RestaurantResponseDto updateRestaurant(User user, Long restaurantId, UpdateRestaurantRequestDto updateRestaurantRequestDto) {
        // 가게 조회
        System.out.println("// ========== Select Query ========== //");
        Restaurant restaurant = getRestaurant(user, restaurantId);

        // 내용 수정
        System.out.println("// ========== Update Query ========== //");
        restaurant.update(updateRestaurantRequestDto);

        return new RestaurantResponseDto(restaurant);
    }

    public void deleteRestaurant(User user, Long restaurantId) {
        Restaurant restaurant = getRestaurant(user, restaurantId);
        restaurant.delete();
    }



    // ============ private 메서드 ============ //

    // 본인 가게 조회 메서드
    private Restaurant getRestaurant(User user, Long restaurantId) {
        return restaurantRepository.findByUserIdAndIdAndIsDeletedFalse(user.getId(), restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 본인 소유의 가게가 아닙니다"));
    }

    // 본인 가게 중 중복 이름 검증 메서드
    private void checkForDuplicateRestaurantName(CreateRestaurantDto createRestaurantDto, User findUser) {
        List<String> findRestaurantNames = restaurantRepository.findRestaurantNameByUserId(findUser.getId());
        if(findRestaurantNames.contains(createRestaurantDto.getName())) {
            throw new IllegalArgumentException("이미 동일한 이름의 가게를 보유중입니다.");
        }
    }
}
