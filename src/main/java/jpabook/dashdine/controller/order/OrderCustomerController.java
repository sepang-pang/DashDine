package jpabook.dashdine.controller.order;

import jpabook.dashdine.dto.request.order.CreateOrderParam;
import jpabook.dashdine.dto.response.ApiResponseDto;
import jpabook.dashdine.dto.response.order.OrderForm;
import jpabook.dashdine.security.userdetails.UserDetailsImpl;
import jpabook.dashdine.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static jpabook.dashdine.domain.user.UserRoleEnum.Authority.CUSTOMER;

@RestController
@RequiredArgsConstructor
@Secured(CUSTOMER)
public class OrderCustomerController {

    private final OrderService orderService;

    @PostMapping("/order")
    public ResponseEntity<ApiResponseDto> createOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @RequestBody CreateOrderParam param) {

        orderService.createOrder(userDetails.getUser(), param);

        return ResponseEntity.ok().body(new ApiResponseDto("주문 성공", HttpStatus.OK.value()));
    }

    @GetMapping("/order")
    public List<OrderForm> readAllOrder(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        return orderService.readAllOrder(userDetails.getUser());
    }


}
