package jpabook.dashdine.service.Restaurant;

import jpabook.dashdine.domain.restaurant.Restaurant;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.domain.user.UserRoleEnum;
import jpabook.dashdine.dto.request.restaurant.CreateRestaurantDto;
import jpabook.dashdine.repository.Restaurant.RestaurantRepository;
import jpabook.dashdine.service.user.UserInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantManagementServiceTest {
    @InjectMocks
    private RestaurantManagementService service;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private UserInfoService userInfoService;

    private User user;

    private CreateRestaurantDto createRestaurantDto;

    @BeforeEach
    void setUp() {
        user = new User("userExample", "encodedNewPassword", "email@example.com", UserRoleEnum.OWNER);
        createRestaurantDto = new CreateRestaurantDto("exampleName1", "000-1111-1111");
    }

    @Nested
    @DisplayName("식당 생성 테스트")
    class testCreateRestaurant {

        @Test
        @DisplayName("생성 실패 : 중복된 이름")
        void failToCreateRestaurantWithDuplicateName() {
            // Given
            when(userInfoService.findUser(user.getLoginId())).thenReturn(user);
            when(restaurantRepository.findRestaurantNameByUserId(user.getId())).thenReturn(asList("exampleName1", "exampleName2"));

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                service.createRestaurant(user, createRestaurantDto);
            });

            verify(restaurantRepository, times(0)).save(any(Restaurant.class));
        }

        @Test
        @DisplayName("생성 성공 : 중복 이름 없음")
        void successToCreateRestaurant(){
            // Given
            when(userInfoService.findUser(user.getLoginId())).thenReturn(user);
            when(restaurantRepository.findRestaurantNameByUserId(user.getId())).thenReturn(asList("exampleName2", "exampleName3"));

            // When
            service.createRestaurant(user, createRestaurantDto);

            // Then
            verify(restaurantRepository, times(1)).save(any(Restaurant.class));
        }
    }
}