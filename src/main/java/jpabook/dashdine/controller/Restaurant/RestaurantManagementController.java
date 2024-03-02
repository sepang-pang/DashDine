package jpabook.dashdine.controller.Restaurant;

import jakarta.validation.Valid;
import jpabook.dashdine.dto.request.restaurant.CreateRestaurantDto;
import jpabook.dashdine.dto.response.ApiResponseDto;
import jpabook.dashdine.security.userdetails.UserDetailsImpl;
import jpabook.dashdine.service.Restaurant.RestaurantManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static jpabook.dashdine.domain.user.UserRoleEnum.Authority.OWNER;


@RestController
@RequiredArgsConstructor
@RequestMapping("/owner")
@Secured(OWNER)
public class RestaurantManagementController {

    private final RestaurantManagementService restaurantManagementService;

    @PostMapping("/create-restaurant")
    public ResponseEntity<ApiResponseDto> createRestaurant(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody @Valid CreateRestaurantDto createRestaurantDto) {
        restaurantManagementService.createRestaurant(userDetails.getUser(), createRestaurantDto);
        return ResponseEntity.ok().body(new ApiResponseDto("가게 생성 성공", HttpStatus.OK.value()));
    }

}
