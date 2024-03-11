package jpabook.dashdine.controller.cart;

import jpabook.dashdine.dto.request.cart.CreateCartRequestDto;
import jpabook.dashdine.dto.request.cart.UpdateCartRequestDto;
import jpabook.dashdine.dto.response.ApiResponseDto;
import jpabook.dashdine.dto.response.cart.CartResponseDto;
import jpabook.dashdine.security.userdetails.UserDetailsImpl;
import jpabook.dashdine.service.cart.CartManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "Cart Management Controller")
public class CartManagementController {

    private final CartManagementService cartManagementService;

    @PostMapping("/cart")
    public ResponseEntity<ApiResponseDto> addCart(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody CreateCartRequestDto createCartRequestDto) {

        cartManagementService.addCart(userDetails.getUser(), createCartRequestDto);

        return ResponseEntity.ok().body(new ApiResponseDto("장바구니 담기 성공", HttpStatus.OK.value()));
    }

    @GetMapping("/cart")
    public CartResponseDto readAllCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        return cartManagementService.readAllCart(userDetails.getUser());
    }

    @PutMapping("/cart/{cartMenuId}")
    public ResponseEntity<ApiResponseDto> updateCart(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable("cartMenuId")Long cartMenuId, @RequestBody UpdateCartRequestDto updateCartRequestDto) {
        cartManagementService.updateCart(userDetails.getUser(), cartMenuId, updateCartRequestDto);
        return ResponseEntity.ok().body(new ApiResponseDto("장바구니 수정 성공", HttpStatus.OK.value()));
    }
}
