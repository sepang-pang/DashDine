package jpabook.dashdine.controller.order;

import jpabook.dashdine.domain.order.OrderStatus;
import jpabook.dashdine.dto.request.order.CancelOrderParam;
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
    public List<OrderForm> readAllOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                        @RequestParam(name = "status", required = false) OrderStatus orderStatus) {
        return orderService.readAllOrder(userDetails.getUser(), orderStatus);
    }

    @PatchMapping("/order/{orderId}/cancel")
    public ResponseEntity<ApiResponseDto> cancelOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @PathVariable("orderId") Long orderId,
                                                      @RequestBody CancelOrderParam param) {

        orderService.cancelOrder(userDetails.getUser(), orderId, param);

        return ResponseEntity.ok().body(new ApiResponseDto("주문 취소 성공", HttpStatus.OK.value()));
    }

    @PatchMapping("/order/{orderId}/delete")
    public ResponseEntity<ApiResponseDto> deleteOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @PathVariable("orderId") Long orderId) {

        orderService.deleteOrderDetails(userDetails.getUser(), orderId);

        return ResponseEntity.ok().body(new ApiResponseDto("주문내역 삭제 성공", HttpStatus.OK.value()));
    }
}
