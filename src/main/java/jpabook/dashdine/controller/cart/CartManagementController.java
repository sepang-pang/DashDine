package jpabook.dashdine.controller.cart;

import jpabook.dashdine.dto.request.cart.AddCartParam;
import jpabook.dashdine.dto.request.cart.UpdateCartParam;
import jpabook.dashdine.dto.response.ApiResponseDto;
import jpabook.dashdine.dto.response.cart.CartForm;
import jpabook.dashdine.security.userdetails.UserDetailsImpl;
import jpabook.dashdine.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "Cart Management Controller")
public class CartManagementController {

    private final CartService cartManagementService;

    @PostMapping("/cart")
    public ResponseEntity<ApiResponseDto> addCart(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody AddCartParam param) {

        cartManagementService.addCart(userDetails.getUser(), param);

        return ResponseEntity.ok().body(new ApiResponseDto("장바구니 담기 성공", HttpStatus.OK.value()));
    }

    @GetMapping("/cart")
    public CartForm readAllCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return cartManagementService.readAllCart(userDetails.getUser());
    }

    @PatchMapping("/cart/cart-menu/{cartMenuId}/menu/{menuId}")
    public ResponseEntity<ApiResponseDto> updateCart(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @PathVariable("cartMenuId")Long cartMenuId,
                                                     @PathVariable("menuId")Long menuId,
                                                     @RequestBody UpdateCartParam updateCartParam) {
        cartManagementService.updateCart(userDetails.getUser(), cartMenuId, menuId, updateCartParam);
        return ResponseEntity.ok().body(new ApiResponseDto("장바구니 수정 성공", HttpStatus.OK.value()));
    }

    @DeleteMapping("/cart/cart-menu/{cartMenuId}")
    public ResponseEntity<ApiResponseDto> deleteCart(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @PathVariable("cartMenuId")Long cartMenuId) {
        cartManagementService.deleteCart(userDetails.getUser(), cartMenuId);
        return ResponseEntity.ok().body(new ApiResponseDto("장바구니 취소 성공", HttpStatus.OK.value()));
    }
}
