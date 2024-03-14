package jpabook.dashdine.controller.order;

import jpabook.dashdine.dto.request.order.CreateOrderRequestDto;
import jpabook.dashdine.dto.response.ApiResponseDto;
import jpabook.dashdine.security.userdetails.UserDetailsImpl;
import jpabook.dashdine.service.order.OrderManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static jpabook.dashdine.domain.user.UserRoleEnum.Authority.CUSTOMER;

@RestController
@RequiredArgsConstructor
@Secured(CUSTOMER)
public class OrderCustomerController {

    private final OrderManagementService orderManagementService;

    @PostMapping("/order")
    public ResponseEntity<ApiResponseDto> createOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @RequestBody CreateOrderRequestDto requestDto) {

        orderManagementService.createOrder(userDetails.getUser(), requestDto.getCartMenuIds());

        return ResponseEntity.ok().body(new ApiResponseDto("주문 성공", HttpStatus.OK.value()));
    }




}