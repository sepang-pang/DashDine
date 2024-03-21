package jpabook.dashdine.controller.restaurant;

import jpabook.dashdine.dto.response.restaurant.RestaurantDetailsForm;
import jpabook.dashdine.service.restaurant.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static jpabook.dashdine.domain.user.UserRoleEnum.Authority.CUSTOMER;
import static jpabook.dashdine.domain.user.UserRoleEnum.Authority.OWNER;

@RestController
@RequiredArgsConstructor
@Secured({OWNER, CUSTOMER})
public class RestaurantCommonController {

    private final RestaurantService restaurantManagementService;

    @GetMapping("/restaurant/{restaurantId}")
    public RestaurantDetailsForm readOneRestaurant(@PathVariable("restaurantId")Long restaurantId) {
        return restaurantManagementService.readOneRestaurant(restaurantId);
    }
}
