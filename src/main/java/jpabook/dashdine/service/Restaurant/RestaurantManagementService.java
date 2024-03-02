package jpabook.dashdine.service.Restaurant;

import jpabook.dashdine.domain.restaurant.Restaurant;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.dto.request.restaurant.CreateRestaurantDto;
import jpabook.dashdine.repository.Restaurant.RestaurantRepository;
import jpabook.dashdine.repository.user.UserRepository;
import jpabook.dashdine.service.user.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        System.out.println("// =========== 유저 조회 =========== //");
        User findUser = userInfoService.findUser(user.getLoginId());

        // 본인이 소유한 가게 중 동일한 이름이 있을 경우 예외 발생
        System.out.println("// =========== 목록 조회 =========== //");
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

        System.out.println("// =========== 저장 =========== //");
        restaurantRepository.save(restaurant);
    }

    private void checkForDuplicateRestaurantName(CreateRestaurantDto createRestaurantDto, User findUser) {
        List<String> findRestaurantNames = restaurantRepository.findRestaurantNameByUserId(findUser.getId());
        if(findRestaurantNames.contains(createRestaurantDto.getName())) {
            throw new IllegalArgumentException("이미 동일한 이름의 가게를 보유중입니다.");
        }
    }
}
