package jpabook.dashdine.controller.restaurant;

import jpabook.dashdine.dto.response.restaurant.RestaurantForm;
import jpabook.dashdine.service.restaurant.RestaurantManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static jpabook.dashdine.domain.user.UserRoleEnum.Authority.CUSTOMER;

@RestController
@RequiredArgsConstructor
@Secured(CUSTOMER)
public class RestaurantCustomerController {

    private final RestaurantManagementService restaurantManagementService;

    @GetMapping("/category/{categoryId}/restaurant")
    public List<RestaurantForm> readAllRestaurant(@PathVariable("categoryId")Long categoryId) {
        return restaurantManagementService.readAllRestaurant(categoryId);
    }
}
