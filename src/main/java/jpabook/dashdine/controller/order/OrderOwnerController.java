package jpabook.dashdine.controller.order;

import jpabook.dashdine.dto.response.ApiResponseDto;
import jpabook.dashdine.security.userdetails.UserDetailsImpl;
import jpabook.dashdine.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static jpabook.dashdine.domain.user.UserRoleEnum.Authority.OWNER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owner")
@Secured(OWNER)
public class OrderOwnerController {

    private final OrderService orderService;

    @PatchMapping("/order/{orderId}")
    public ResponseEntity<ApiResponseDto> receiveOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @PathVariable("orderId")Long orderId) {

        orderService.receiveOrder(userDetails.getUser(), orderId);

        return ResponseEntity.ok().body(new ApiResponseDto("주문 접수 완료", HttpStatus.OK.value()));
    }
}
