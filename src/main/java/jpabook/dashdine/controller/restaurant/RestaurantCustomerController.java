package jpabook.dashdine.controller.restaurant;

import jpabook.dashdine.dto.request.restaurant.RadiusCondition;
import jpabook.dashdine.dto.response.comment.RestaurantReviewForm;
import jpabook.dashdine.dto.response.restaurant.RestaurantForm;
import jpabook.dashdine.security.userdetails.UserDetailsImpl;
import jpabook.dashdine.service.comment.ReviewService;
import jpabook.dashdine.service.restaurant.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static jpabook.dashdine.domain.user.UserRoleEnum.Authority.CUSTOMER;

@RestController
@RequiredArgsConstructor
@Secured(CUSTOMER)
public class RestaurantCustomerController {

    private final RestaurantService restaurantManagementService;
    private final ReviewService reviewService;

    @GetMapping("/category/{categoryId}/restaurant")
    public List<RestaurantForm> readAllRestaurant(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  @PathVariable("categoryId")Long categoryId,
                                                  @RequestBody RadiusCondition cond) {

        return restaurantManagementService.readAllRestaurant(userDetails.getUser(), categoryId, cond);
    }

    @GetMapping("/restaurant/{restaurantId}/review")
    public List<RestaurantReviewForm> readAllReviewFromRestaurant(@PathVariable("restaurantId")Long restaurantId) {
        return reviewService.readAllReviewFromRestaurant(restaurantId);
    }
}
