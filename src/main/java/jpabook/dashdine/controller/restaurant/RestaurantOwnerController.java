package jpabook.dashdine.controller.restaurant;

import jakarta.validation.Valid;
import jpabook.dashdine.advice.custom.ResourceNotFoundException;
import jpabook.dashdine.dto.request.restaurant.CreateRestaurantParam;
import jpabook.dashdine.dto.request.restaurant.UpdateRestaurantParam;
import jpabook.dashdine.dto.response.ApiResponseDto;
import jpabook.dashdine.dto.response.comment.RestaurantReviewForm;
import jpabook.dashdine.dto.response.restaurant.RestaurantForm;
import jpabook.dashdine.security.userdetails.UserDetailsImpl;
import jpabook.dashdine.service.comment.ReviewService;
import jpabook.dashdine.service.restaurant.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static jpabook.dashdine.domain.user.UserRoleEnum.Authority.OWNER;


@RestController
@RequiredArgsConstructor
@RequestMapping("/owner")
@Secured(OWNER)
public class RestaurantOwnerController {

    private final RestaurantService restaurantManagementService;
    private final ReviewService reviewManagementService;

    @PostMapping("/restaurant")
    public ResponseEntity<ApiResponseDto> createRestaurant(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody @Valid CreateRestaurantParam param) throws ParseException {
        restaurantManagementService.createRestaurant(userDetails.getUser(), param);
        return ResponseEntity.ok().body(new ApiResponseDto("가게 생성 성공", HttpStatus.OK.value()));
    }

    @GetMapping("/restaurant")
    public List<RestaurantForm> readAllRestaurant(@AuthenticationPrincipal UserDetailsImpl userDetails) throws ResourceNotFoundException {
        return restaurantManagementService.readAllRestaurant(userDetails.getUser());
    }

    @GetMapping("/restaurant/{restaurantId}")
    public RestaurantForm readOneRestaurant(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @PathVariable("restaurantId")Long restaurantId) {
        return restaurantManagementService.readOneRestaurant(userDetails.getUser(), restaurantId);
    }

    @GetMapping("/restaurant/review")
    public List<RestaurantReviewForm> readAllReviewFromRestaurant(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reviewManagementService.readAllReviewFromUser(userDetails.getUser());
    }

    @PutMapping("/restaurant/{restaurantId}")
    public RestaurantForm updateRestaurant(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable("restaurantId")Long restaurantId, @RequestBody UpdateRestaurantParam param) throws ParseException {
        return restaurantManagementService.updateRestaurant(userDetails.getUser(), restaurantId, param);
    }

    @PatchMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponseDto> deleteRestaurant(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable("restaurantId")Long restaurantId) {
        restaurantManagementService.deleteRestaurant(userDetails.getUser(), restaurantId);
        return ResponseEntity.ok().body(new ApiResponseDto("가게 삭제 성공", HttpStatus.OK.value()));
    }
}
