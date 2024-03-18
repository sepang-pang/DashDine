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
        long startTime = System.nanoTime(); // 시작 시간 측정
        List<OrderForm> orderForms = orderService.readAllOrder(userDetails.getUser());
        long endTime = System.nanoTime(); // 종료 시간 측정
        double executionTime = (endTime - startTime) / 1_000_000.0; // 나노초를 밀리초로 변환
        System.out.printf("Execution time: %.2f ms\n", executionTime); // 실행 시간 출력
        return orderForms;
    }


}
